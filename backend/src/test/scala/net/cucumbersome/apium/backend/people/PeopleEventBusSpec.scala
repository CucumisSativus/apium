package net.cucumbersome.apium.backend.people
import org.specs2.matcher.MatchResult
import scalaz.zio.Task
import scalaz.zio._

class PeopleEventBusSpec extends org.specs2.mutable.Specification with Person.PersonOps{
  "people event bus" >> {
    "should handle people created event properly" >> {
      val person = Person("name".toName)
      inScope{ testScope =>
        for{
          (peopleRef, repo) <- testScope.peopleAndMock
          bus = new PeopleEventBus(repo)
          _ <- bus.publishEvent(PersonCreated(person))
          peopleAfterEvent <- peopleRef.get
        } yield peopleAfterEvent should_=== List(person)
      }
    }
  }

  trait TestScope{
    def buildRepo(createdPeople: Ref[List[Person]]): PeopleRepositoryWrite =
      (person: Person) => for {
      _ <- createdPeople.update(_ :+ person)
    } yield ()

    val peopleAndMock: UIO[(Ref[List[Person]], PeopleRepositoryWrite)] = for{
      ref <- Ref.make(List.empty[Person])
      repo = buildRepo(ref)
    } yield (ref, repo)
  }

  def inScope[T](f: TestScope => Task[MatchResult[T]]): MatchResult[T] = {
    new DefaultRuntime {}.unsafeRun( f(new TestScope {}))
  }
}