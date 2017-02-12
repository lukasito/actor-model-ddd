package org.jellysource.domain.model

import akka.actor.ActorRef

object PersonEvents {

  case class PersonCreated(id: ActorRef)

  case class PersonNotFound()

  case class PersonFound(person: ActorRef)

}
