package org.jellysource.application

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import org.jellysource.domain.model.Person.Create
import org.jellysource.domain.model.PersonEvents.{Created, Updated}
import org.jellysource.domain.repository.PersonRepository.Send
import org.jellysource.infrastructure.InMemoryPersonRepository

object Application extends App {

  class Controller(personRepository: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
      case Created(information) =>
        log info s"Person created [$information]"
      case Updated(newInfo, oldInfo) =>
        log info s"Person updated from [$oldInfo] to [$newInfo]"
    }
  }

  val actorSystem = ActorSystem("personSystem")
  val personRepositoryRef = actorSystem.actorOf(InMemoryPersonRepository.props, "personRepository")
  val controller = actorSystem.actorOf(Props(new Controller(personRepositoryRef)), "controller")


  personRepositoryRef ! Send(UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"), Create())
  personRepositoryRef ! Send(UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94"), Save())
}
