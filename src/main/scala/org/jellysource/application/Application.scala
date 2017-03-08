package org.jellysource.application

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import org.jellysource.domain.events.DomainEventPublisher
import org.jellysource.domain.events.DomainEventPublisher.Subscribe
import org.jellysource.domain.events.PersonEvents.Stored
import org.jellysource.domain.model.Person.SetFirstName
import org.jellysource.domain.repository.PersonRepository.Send
import org.jellysource.infrastructure.InMemoryPersonRepository

object Application extends App {

  class Probe(eventPublisher: ActorRef) extends Actor with ActorLogging {

    override def preStart: Unit = {
      eventPublisher ! Subscribe("person-events")
    }

    override def receive: Receive = {
      case Stored(information) => log info information.toString
    }
  }

  val actorSystem = ActorSystem("personSystem")
  val domainEventPublisher = actorSystem.actorOf(Props(new DomainEventPublisher), "domainEventPublisher")
  val probe = actorSystem.actorOf(Props(new Probe(domainEventPublisher)), "probe")
  val personRepositoryRef = actorSystem.actorOf(InMemoryPersonRepository.props(domainEventPublisher), "personRepository")

  private val existingPerson = UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94")
  private val notExistingPerson = UUID.randomUUID()

  personRepositoryRef ! Send(existingPerson, SetFirstName("some other name"))
  personRepositoryRef ! Send(notExistingPerson, SetFirstName("some other name"))
}
