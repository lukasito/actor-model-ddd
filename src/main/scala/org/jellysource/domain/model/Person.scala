package org.jellysource.domain.model

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import org.jellysource.domain.model.Person._
import org.jellysource.domain.model.PersonEvents._
import org.jellysource.domain.model.Validations.{AddressValidation, PhoneNumberValidation}
import org.jellysource.domain.repository.PersonRepository

object Person {

  def props(uuid: UUID): Props = {
    Props(new Person(uuid))
  }

  case class PersonalInformation(
    firstName: String,
    lastName: String,
    address: String,
    phoneNumber: String
  )

  case class Create(personalInformation: PersonalInformation)
  case class Update(personalInformation: PersonalInformation)
  case class SetFirstName(firstName: String)
}

class Person(id: UUID) extends Actor with AddressValidation with PhoneNumberValidation {

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
    case SetFirstName(firstName) =>
      val newInfo = personalInformation copy (firstName = firstName)
      context become created(newInfo)
      sender ! Updated(newInfo, personalInformation)
  }
}
