package org.jellysource.domain.events

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.event._
import org.jellysource.domain.events.DomainEventPublisher.{Subscribe, Unsubscribe}

object DomainEventPublisher {

  case class Subscribe(classifier: String)

  case class Unsubscribe(classifier: String)

}

class DomainEventPublisher extends Actor
  with ActorLogging
  with ActorEventBus
  with LookupClassification {

  override type Event = DomainEvent
  override type Classifier = String

  override protected def mapSize(): Int = 10 // number of different event types

  override protected def classify(event: DomainEvent): String = event.classifier

  override protected def publish(event: DomainEvent, subscriber: ActorRef): Unit = {
    subscriber ! event
  }

  override def receive: Receive = {
    case domainEvent: DomainEvent =>
      log info s"publishing event from: $sender, origin: ${domainEvent.origin()}"
      publish(domainEvent)

    case Subscribe(classifier) =>
      log info s"subscribing for $classifier from $sender"
      subscribe(sender, classifier)

    case Unsubscribe(classifier) =>
      log info s"unsubscribing for $classifier from $sender"
      unsubscribe(sender, classifier)
  }
}
