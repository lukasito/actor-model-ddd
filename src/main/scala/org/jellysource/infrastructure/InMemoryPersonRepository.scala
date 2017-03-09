package org.jellysource.infrastructure

import java.lang.Comparable
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.jellysource.domain.events.DomainEventPublisher.Subscribe
import org.jellysource.domain.events.PersonEvents.{Stored, Updated}
import org.jellysource.domain.model.Person
import org.jellysource.domain.model.Person.{Create, PersonalInformation}
import org.jellysource.domain.repository.PersonRepository.Send
import org.jellysource.infrastructure.InMemoryPersonRepository.InMemoryPerson

object InMemoryPersonRepository {

  def props(eventPublisher: ActorRef): Props = {
    Props(new InMemoryPersonRepository(eventPublisher))
  }

  private case class InMemoryPerson(id: UUID, personalInformation: PersonalInformation) {
    override def hashCode(): Int = {
      val prime = 19273
      var result = 1
      prime * result + id.hashCode
    }

    override def equals(obj: scala.Any): Boolean = {
      obj match {
        case InMemoryPerson(uuid, _) => id.equals(uuid)
        case _ => false
      }
    }
  }
}

class InMemoryPersonRepository(eventPublisher: ActorRef) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    eventPublisher ! Subscribe("person-events")
  }

  private val person1: InMemoryPerson = InMemoryPerson(
    UUID.fromString("5bbd7d88-dfd4-4457-9706-50e26908aa07"),
    PersonalInformation("firstName1", "lastName1", "address1", "phoneNumber1")
  )

  private val person2: InMemoryPerson = InMemoryPerson(
    UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"),
    PersonalInformation("firstName2", "lastName2", "address2", "phoneNumber2")
  )

  private var inMemoryDatabase: Set[InMemoryPerson] = Set(person1, person2)
  private var persons: Map[UUID, ActorRef] = Map.empty

  override def receive: Receive = {
    case Send(uuid, message) =>
      log.info("Finding actor..")
      inMemoryDatabase.find(p => p.id.equals(uuid)) match {
        case Some(person) =>
          log.info("Found person in db!")
          val personRef = persons.get(person.id) match {
            case Some(pRef) =>
              log.info("Found person actor in memory!")
              pRef
            case None =>
              log.info("Not found person actor in memory, registering new actor...")
              val pRef = context.actorOf(Person.props(uuid, eventPublisher), uuid.toString)
              persons += (uuid -> pRef)
              pRef ! Create(person.personalInformation)
              pRef
          }
          personRef ! message
        case None =>
          log.info("Person not found!")
      }
    case msg @ Updated(pi1, _) =>
      val personId = UUID.fromString(msg.origin.path.name)
      log.info(s"Storing personal information of person-id: ${personId.toString}")
      inMemoryDatabase += InMemoryPerson(personId, pi1)
      eventPublisher ! Stored(pi1)
  }
}
