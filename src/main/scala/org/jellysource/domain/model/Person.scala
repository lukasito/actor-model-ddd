package org.jellysource.domain.model

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.jellysource.domain.events.PersonEvents._
import org.jellysource.domain.model.Person._
import org.jellysource.domain.model.Validations.{AddressValidation, PhoneNumberValidation}

object Person {

  def props(uuid: UUID, eventPublisher: ActorRef): Props = {
    Props(new Person(uuid, eventPublisher))
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

class Person(id: UUID, eventPublisher: ActorRef) extends Actor
  with AddressValidation
  with PhoneNumberValidation
  with ActorLogging {

  override def receive: Receive = {
    case Create(personalInfo) =>
      validateAddress(personalInfo.address)
      validatePhoneNumber(personalInfo.phoneNumber)
      context.become(created(personalInfo))
      log info "Publishing created event!"
      eventPublisher ! Created(personalInfo)
  }

  private def created(personalInformation: PersonalInformation): Receive = {
    case Update(newPersonalInfo) =>
      context become created(newPersonalInfo)
      eventPublisher ! Updated(newPersonalInfo, personalInformation)
    case SetFirstName(firstName) =>
      val newInfo = personalInformation copy (firstName = firstName)
      context become created(newInfo)
      log info "Publishing updated event!"
      eventPublisher ! Updated(newInfo, personalInformation)
  }
}
