package org.jellysource.domain.events

import akka.actor.ActorRef

/**
  * Marking interface
  */
trait DomainEvent {
  def origin()(implicit origin: ActorRef): ActorRef = origin
  def classifier: String
}
