package org.jellysource.domain.repository

import java.util.UUID

import org.jellysource.domain.model.Person.PersonalInformation

object PersonRepository {

  case class Send(personId: UUID, message: Any)

  case class Store(personalInformation: PersonalInformation)

}


