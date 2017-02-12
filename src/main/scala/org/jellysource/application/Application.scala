package org.jellysource.application

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import org.jellysource.domain.model.Person.{Save, SetFirstName}
import org.jellysource.domain.model.PersonEvents.{PersonFound, PersonNotFound}
import org.jellysource.domain.repository.PersonRepository.Send
import org.jellysource.infrastructure.InMemoryPersonRepository

object Application extends App {

  class Controller(personRepository: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
      case PersonFound(personRef) =>
        personRef ! SetFirstName("updated first name")
        personRef ! Save()
        log info "Controller found person"
      case PersonNotFound() =>
        log info "Controller not found person"
    }
  }

  val actorSystem = ActorSystem("personSystem")
  val personRepositoryRef = actorSystem.actorOf(InMemoryPersonRepository.props, "personRepository")
  val controller = actorSystem.actorOf(Props(new Controller(personRepositoryRef)), "controller")


  personRepositoryRef ! Send(UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"), SetFirstName("update_firstName"))
  personRepositoryRef ! Send(UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"), Save())
}
