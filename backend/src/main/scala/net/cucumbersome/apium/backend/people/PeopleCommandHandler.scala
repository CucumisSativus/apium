package net.cucumbersome.apium.backend.people

import cats.data.NonEmptyList
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.{CommandError, CreatePerson}
import net.cucumbersome.apium.backend.people.validators.CreatePersonValidator
import scalaz.zio.ZIO


trait PeopleCommandHandler{
  def handle(createPerson: CreatePerson): ZIO[PeopleEventBus, CommandError, Unit]
}
object PeopleCommandHandler extends Person.PersonOps {

  def apply: PeopleCommandHandler = new PeopleCommandHandlerImpl


  private class PeopleCommandHandlerImpl extends PeopleCommandHandler{
    override def handle(createPerson: CreatePerson): ZIO[PeopleEventBus, CommandError, Unit] = {
      for{
        res <- validateCreatePerson(createPerson)
        _ <- publishEvent(PersonCreated(res))
      } yield ()
    }
  }

  def validateCreatePerson(createPerson: CreatePerson):ZIO[PeopleEventBus, CommandError, Person] =
    ZIO.fromEither(CreatePersonValidator.validateCommand(createPerson).toEither)
      .mapError{ errors => ValidationError(errors.toNonEmptyList.map(_.message))}

  case class CreatePerson(id: String, name: String)

  sealed trait CommandError

  case class ValidationError(messages: NonEmptyList[String]) extends CommandError
}
