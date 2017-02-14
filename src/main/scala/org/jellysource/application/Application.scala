package org.jellysource.application

import java.util.UUID

import akka.actor.ActorSystem
import org.jellysource.domain.model.Person.SetFirstName
import org.jellysource.domain.repository.PersonRepository.Send
import org.jellysource.infrastructure.InMemoryPersonRepository

object Application extends App {

  val actorSystem = ActorSystem("personSystem")
  val personRepositoryRef = actorSystem.actorOf(InMemoryPersonRepository.props, "personRepository")

  private val existingPerson = UUID.fromString("301f27c8-944b-4f35-8624-ce6900d27c94")
  private val notExistingPerson = UUID.randomUUID()

  personRepositoryRef ! Send(existingPerson, SetFirstName("some other name"))
  personRepositoryRef ! Send(notExistingPerson, SetFirstName("some other name"))
}
