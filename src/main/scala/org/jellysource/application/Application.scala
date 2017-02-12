package org.jellysource.application

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import org.jellysource.domain.model.Person.SetFirstName
import org.jellysource.domain.model.PersonEvents.{PersonFound, PersonNotFound}
import org.jellysource.domain.repository.PersonRepository.Find
import org.jellysource.infrastructure.InMemoryPersonRepository

object Application extends App {

  class Controller(personRepository: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
      case PersonFound(personRef) =>
        personRef ! SetFirstName("updated first name")

        log info "Controller found person"
      case PersonNotFound() =>
        log info "Controller not found person"
    }
  }

  val actorSystem = ActorSystem("personSystem")
  val personRepositoryRef = actorSystem.actorOf(InMemoryPersonRepository.props, "personRepository")
  val controller = actorSystem.actorOf(Props(new Controller(personRepositoryRef)), "controller")


  personRepositoryRef ! (Find(UUID.randomUUID()), controller)
}
