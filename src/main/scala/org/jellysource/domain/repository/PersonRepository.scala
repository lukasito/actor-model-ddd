package org.jellysource.domain.repository

import java.util.UUID

import org.jellysource.domain.model.Person.PersonalInformation

object PersonRepository {

  case class Find(personId: UUID)

  case class Store(personalInformation: PersonalInformation)

}


