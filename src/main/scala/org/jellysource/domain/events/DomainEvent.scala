package org.jellysource.domain.events

import akka.actor.ActorRef

/**
  * Marking interface
  */
trait DomainEvent {
  implicit val origin: ActorRef
  val classifier: String
}
