package net.cucumbersome.apium.backend.people

import net.cucumbersome.apium.backend.people.PeopleCommandHandler.CreatePerson
import org.specs2.matcher.MatchResult
import scalaz.zio.{DefaultRuntime, Ref, UIO, ZIO}

class PeopleCommandHandlerSpec extends org.specs2.mutable.Specification with Person.PersonOps{
  "people command handler" >> {
    "should emit an event on succesfull person creation" >> {
      val expectedPerson = Person("id1".toId, "name".toName)
      val command = CreatePerson("id1", "name")
      inScope{ testScope =>
        for{
          (ref, bus) <- testScope.busAndMock
          handler = PeopleCommandHandler.apply
          _ <- handler.handle(command).provide(bus)
          obtainedEvents <- ref.get
        } yield obtainedEvents should_=== List(PersonCreated(expectedPerson))
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

  def inScope[T](f: TestScope => ZIO[Any, Any, MatchResult[T]]): MatchResult[T] = {
    new DefaultRuntime {}.unsafeRun( f(new TestScope {}))
  }
}
