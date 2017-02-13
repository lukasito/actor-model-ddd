package org.jellysource.domain.model

import org.jellysource.domain.model.Person.PersonalInformation

object PersonEvents {

  case class Created(personalInformation: PersonalInformation)

  case class Updated(newInfo: PersonalInformation, oldInfo: PersonalInformation)

  case class Stored(personalInformation: PersonalInformation)

}
