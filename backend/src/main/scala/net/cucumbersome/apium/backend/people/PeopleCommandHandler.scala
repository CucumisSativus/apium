package net.cucumbersome.apium.backend.people

import cats.data.NonEmptyList
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.{CommandError, CreatePerson}
import scalaz.zio.ZIO


trait PeopleCommandHandler{
  def handle(createPerson: CreatePerson): ZIO[PeopleEventBus, CommandError, Unit]
}
object PeopleCommandHandler extends Person.PersonOps {

  def apply: PeopleCommandHandler = new PeopleCommandHandlerImpl


  private class PeopleCommandHandlerImpl extends PeopleCommandHandler{
    override def handle(createPerson: CreatePerson): ZIO[PeopleEventBus, CommandError, Unit] = {
      val person = Person(id = createPerson.id.toId, name = createPerson.name.toName)

      for{
        res <- ZIO.succeed(())
        _ <- publishEvent(PersonCreated(person))
      } yield res
    }
  }
  case class CreatePerson(id: String, name: String)

  case class CommandError(errors: NonEmptyList[String])
}
