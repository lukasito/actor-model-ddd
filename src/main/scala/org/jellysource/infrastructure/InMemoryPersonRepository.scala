package org.jellysource.infrastructure

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.jellysource.domain.model.Person
import org.jellysource.domain.model.Person.{Init, PersonalInformation}
import org.jellysource.domain.model.PersonEvents.{PersonCreated, PersonFound, PersonNotFound}
import org.jellysource.domain.repository.PersonRepository.{Find, Store}
import org.jellysource.infrastructure.InMemoryPersonRepository.InMemoryPerson

object InMemoryPersonRepository {

  def props: Props = {
    Props(new InMemoryPersonRepository)
  }

  private case class InMemoryPerson(id: UUID, personalInformation: PersonalInformation)

}

class InMemoryPersonRepository extends Actor with ActorLogging {

  private val person1: InMemoryPerson = InMemoryPerson(
    UUID.fromString("5bbd7d88-dfd4-4457-9706-50e26908aa07"),
    PersonalInformation(
      Some("firstName1"), Some("lastName1"), Some("address1"), Some("phoneNumber1")
    )
  )

  private val person2: InMemoryPerson = InMemoryPerson(
    UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"),
    PersonalInformation(
      Some("firstName2"), Some("lastName2"), Some("address2"), Some("phoneNumber2")
    )
  )

  private var inMemoryDatabase: List[InMemoryPerson] = List(person1, person2)
  private var persons: Map[UUID, ActorRef] = Map.empty

  override def receive: Receive = {
    case Find(uuid) =>
      log.info("Finding actor..")
      inMemoryDatabase.find(p => p.id.equals(uuid)) match {
        case Some(person) =>
          log.info("Found person in db!")
          val personRef = persons.get(uuid) match {
            case Some(pRef) =>
              log.info("Found person actor in memory!")
              pRef
            case None =>
              log.info("Not found person actor in memory, registering new actor...")
              val pRef = context.actorOf(Person.props(uuid), uuid.toString)
              persons += (uuid -> pRef)
              pRef
          }
          log info "Initializing actor..."
          personRef ! Init(person.personalInformation)
          log info "Person found!"
          sender ! PersonFound(personRef)
        case None =>
          log.info("Person not found!")
          sender ! PersonNotFound()
      }
    case Store(information) =>
      log.info("Storing personal information..")
      val id = UUID.randomUUID()
      val actorRef = context.actorOf(Person.props(id), id.toString)
      persons += (id -> actorRef)
      inMemoryDatabase ::= InMemoryPerson(id, information)
      actorRef ! Init(information)
      log.info("Stored, person created")
      sender ! PersonCreated(actorRef)

  }
}
