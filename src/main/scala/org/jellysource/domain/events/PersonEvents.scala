package org.jellysource.domain.events

import org.jellysource.domain.model.Person.PersonalInformation

object PersonEvents {

  trait PersonClassifier {
    val classifier: String = "person-events"
  }

  case class Created(personalInformation: PersonalInformation) extends DomainEvent with PersonClassifier
  case class Updated(newInfo: PersonalInformation, oldInfo: PersonalInformation) extends DomainEvent with PersonClassifier
  case class Stored(personalInformation: PersonalInformation) extends DomainEvent with PersonClassifier

}
