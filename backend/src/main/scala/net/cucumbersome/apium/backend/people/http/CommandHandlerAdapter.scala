package net.cucumbersome.apium.backend.people.http

import net.cucumbersome.apium.backend.people.{PeopleCommandHandler, PeopleEventBus}
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.{CommandError, CreatePerson}
import scalaz.zio.ZIO

trait CommandHandlerAdapter{
  def handle(createPerson: CreatePerson): ZIO[Any, CommandError, Unit]
}

object CommandHandlerAdapter{
  def apply(peopleCommandHandler: PeopleCommandHandler, eventBus: PeopleEventBus): CommandHandlerAdapter
  = new CommandHandlerAdapterImpl(peopleCommandHandler, eventBus)

  private class CommandHandlerAdapterImpl(peopleCommandHandler: PeopleCommandHandler, eventBus: PeopleEventBus) extends CommandHandlerAdapter {
    override def handle(createPerson: CreatePerson): ZIO[Any, CommandError, Unit] =
      peopleCommandHandler.handle(createPerson).provide(eventBus)
  }
}

