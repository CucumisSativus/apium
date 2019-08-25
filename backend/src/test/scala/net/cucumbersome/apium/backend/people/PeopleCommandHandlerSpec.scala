package net.cucumbersome.apium.backend.people

import cats.data.NonEmptyList
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.{CreatePerson, ValidationError}
import org.specs2.matcher.MatchResult
import zio.{DefaultRuntime, Ref, UIO, ZIO}

class PeopleCommandHandlerSpec extends org.specs2.mutable.Specification with Person.PersonOps{
  "people command handler" >> {
    "should emit an event on succesfull person creation" >> {
      val expectedPerson = Person("612157f7-11d0-4b0c-b914-419fb39e2e53".toId, "name".toName)
      val command = CreatePerson("612157f7-11d0-4b0c-b914-419fb39e2e53", "name")
      inScopeRun{ testScope =>
        for{
          (ref, bus) <- testScope.busAndMock
          handler = PeopleCommandHandler.apply
          _ <- handler.handle(command).provide(bus)
          obtainedEvents <- ref.get
        } yield obtainedEvents should_=== List(PersonCreated(expectedPerson))
      }
    }

    "should return command error on failed validation" >> {
      val command = CreatePerson("invalidId", "")

      val expectedError = ValidationError(NonEmptyList.of("Invalid Id", "Invalid name"))

      inScopeRun{ testScope =>
        for{
          (ref, bus) <- testScope.busAndMock
          handler = PeopleCommandHandler.apply
          res <- handler.handle(command).provide(bus).either
          obtainedEvents <- ref.get
        } yield {
          (obtainedEvents should_=== List.empty) and
            (res.left.get should_=== expectedError)
        }

      }
    }
  }

  trait TestScope{
    def buildBus(events: Ref[List[Event]]): PeopleEventBus = {
      new PeopleEventBus {
        override def service: PeopleEventBus.PeopleEventBusService = {
          event: Event => for {
            _ <- events.update(_ :+ event)
          } yield ()
        }
      }
    }

    val busAndMock: UIO[(Ref[List[Event]], PeopleEventBus)] = for{
      ref <- Ref.make(List.empty[Event])
      bus = buildBus(ref)
    } yield (ref, bus)
  }

  def inScopeRun[T](f: TestScope => ZIO[Any, Any, MatchResult[T]]): MatchResult[T] = {
    new DefaultRuntime {}.unsafeRun( f(new TestScope {}))
  }
}
