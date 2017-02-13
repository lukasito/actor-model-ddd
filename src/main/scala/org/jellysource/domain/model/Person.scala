package org.jellysource.domain.model

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import org.jellysource.domain.model.Person._
import org.jellysource.domain.model.PersonEvents._
import org.jellysource.domain.model.Validations.{AddressValidation, PhoneNumberValidation}
import org.jellysource.domain.repository.PersonRepository

object Person {

  def props(uuid: UUID, personRepository: ActorRef): Props = {
    Props(new Person(uuid, personRepository))
  }

  case class PersonalInformation(
    firstName: String,
    lastName: String,
    address: String,
    phoneNumber: String
  )

  case class Create(personalInformation: PersonalInformation)
  case class Update(personalInformation: PersonalInformation)
  case class Store()
}

class Person(id: UUID, personRepository: ActorRef) extends Actor with AddressValidation with PhoneNumberValidation {

  // TODO how to FIND from repo.
  override def receive: Receive = {
    case Create(personalInfo) =>
      validateAddress(personalInfo.address)
      validatePhoneNumber(personalInfo.phoneNumber)
      context.become(created(personalInfo))
      sender ! Created(personalInfo)
  }

  private def created(personalInformation: PersonalInformation): Receive = {
    case Update(newPersonalInfo) =>
      context become created(newPersonalInfo)
      sender ! Updated(newPersonalInfo, personalInformation)
    case Store() =>
      personRepository ! (PersonRepository.Store(personalInformation), sender)
  }
}
