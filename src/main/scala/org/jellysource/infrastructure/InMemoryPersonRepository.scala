package org.jellysource.infrastructure

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.jellysource.domain.model.Person
import org.jellysource.domain.model.Person.PersonalInformation
import org.jellysource.domain.model.PersonEvents.Stored
import org.jellysource.domain.repository.PersonRepository.{Send, Store}
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
    PersonalInformation("firstName1", "lastName1", "address1", "phoneNumber1")
  )

  private val person2: InMemoryPerson = InMemoryPerson(
    UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"),
    PersonalInformation("firstName2", "lastName2", "address2", "phoneNumber2")
  )

  private var inMemoryDatabase: List[InMemoryPerson] = List(person1, person2)
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
              val pRef = context.actorOf(Person.props(uuid, self), uuid.toString)
              persons += (uuid -> pRef)
              pRef
          }
          personRef forward message
        case None =>
          log.info("Person not found!")
      }
    case Store(personalInformation) =>
      log.info("Storing personal information..")
      val personId = UUID.fromString(sender.path.name)
      inMemoryDatabase ::= InMemoryPerson(personId, personalInformation)
      sender ! Stored(personalInformation)
  }
}
