package org.jellysource.domain.model

import java.util.UUID

import akka.actor.{Actor, Props}
import org.jellysource.domain.model.Person._
import org.jellysource.domain.model.Validations.{AddressValidation, PhoneNumberValidation}
import org.jellysource.domain.repository.PersonRepository.Store

object Person {

  def props(uuid: UUID): Props = {
    Props(new Person(uuid))
  }

  case class PersonalInformation(
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    address: Option[String] = None,
    phoneNumber: Option[String] = None
  )

  case class Init(personalInformation: PersonalInformation)
  case class SetFirstName(firstName: String)
  case class SetLastName(lastName: String)
  case class SetAddress(address: String)
  case class SetPhoneNumber(phone: String)
  case class Save()
}

class Person(id: UUID) extends Actor with AddressValidation with PhoneNumberValidation {
  private var personalInformation = PersonalInformation()

  override def receive: Receive = {
    case Init(personalInfo) =>
      validateAddress(personalInfo.address)
      validatePhoneNumber(personalInfo.phoneNumber)
      context.become(initialized(personalInfo))
  }

  private def initialized(personalInformation: PersonalInformation): Receive = {
    case SetFirstName(fn) => this.personalInformation = personalInformation.copy(firstName = Some(fn))
    case SetLastName(ln) => this.personalInformation = personalInformation.copy(lastName = Some(ln))
    case SetAddress(a) => this.personalInformation = personalInformation.copy(address = Some(a))
    case SetPhoneNumber(pn) => this.personalInformation = personalInformation.copy(phoneNumber = Some(pn))
    case Save() => context.parent ! Store(this.personalInformation)
  }
}
