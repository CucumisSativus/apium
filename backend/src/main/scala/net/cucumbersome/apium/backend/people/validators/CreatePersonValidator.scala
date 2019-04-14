package net.cucumbersome.apium.backend.people.validators

import cats.data._
import cats.data.Validated._
import cats.implicits._
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.CreatePerson
import net.cucumbersome.apium.backend.people.Person

object CreatePersonValidator extends Person.PersonOps {
  type ValidationResult[A] = ValidatedNec[ValidationError, A]
  private val uuidRegexp = "^[a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12}\\Z"

  private def validateId(id: String): ValidationResult[Person.Id] =
    if(id.matches(uuidRegexp)) id.toId.validNec
    else InvalidId.invalidNec

  private def validateName(name: String): ValidationResult[Person.Name] =
    if(name.nonEmpty) name.toName.validNec
    else InvalidName.invalidNec

  def validateCommand(comand: CreatePerson): ValidationResult[Person] =
    (validateId(comand.id), validateName(comand.name)).mapN(Person.apply)

  sealed trait ValidationError{ def message: String }

  case object InvalidId extends ValidationError {
    override def message: String = "Invalid Id"
  }
  case object InvalidName extends ValidationError {
    override def message: String = "Invalid name"
  }
}
