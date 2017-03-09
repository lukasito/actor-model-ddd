package org.jellysource.domain.events

import akka.actor.ActorRef
import org.jellysource.domain.model.Person.PersonalInformation

object PersonEvents {

  trait PersonEvent extends DomainEvent {
    val classifier: String = "person-events"
  }

  case class Created(personalInformation: PersonalInformation)(implicit val origin: ActorRef) extends PersonEvent

  case class Updated(newInfo: PersonalInformation, oldInfo: PersonalInformation)(implicit val origin: ActorRef) extends PersonEvent

  case class Stored(personalInformation: PersonalInformation)(implicit val origin: ActorRef) extends PersonEvent

}
