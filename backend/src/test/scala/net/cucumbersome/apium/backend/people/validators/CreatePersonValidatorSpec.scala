package net.cucumbersome.apium.backend.people.validators

import net.cucumbersome.apium.backend.people.PeopleCommandHandler.CreatePerson
import net.cucumbersome.apium.backend.people.Person
import cats.implicits._

class CreatePersonValidatorSpec extends org.specs2.mutable.Specification with Person.PersonOps{
  "Create person validator" >> {
    "should validate Id" >> {
      val command = CreatePerson("invalidId", "validName")

      val expected = CreatePersonValidator.InvalidId.invalidNec

      CreatePersonValidator.validateCommand(command) should_=== expected
    }

    "should validate name" >> {
      val command = CreatePerson("612157f7-11d0-4b0c-b914-419fb39e2e53", "")

      val expected = CreatePersonValidator.InvalidName.invalidNec

      CreatePersonValidator.validateCommand(command) should_=== expected
    }
  }
}
