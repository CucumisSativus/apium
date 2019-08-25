package net.cucumbersome.apium.backend.people
import net.cucumbersome.apium.backend.people.PeopleEventBus.PeopleEventBusService
import zio._

trait PeopleEventBus{
  def service: PeopleEventBusService
}
object PeopleEventBus{
  trait PeopleEventBusService{
    def publishEvent(event: Event): UIO[Unit]
  }

  def apply(repo: PeopleRepositoryWrite): PeopleEventBus = new LivePeopleEventBus(repo)

  private final class LivePeopleEventBus(repo: PeopleRepositoryWrite) extends PeopleEventBus{
    override val service: PeopleEventBusService = new LiveEventBusService(repo)
  }

  private final class LiveEventBusService(repository: PeopleRepositoryWrite) extends PeopleEventBusService {
    def publishEvent(event: Event): UIO[Unit] = event match {
      case PersonCreated(person) => repository.addPerson(person)
    }
  }
}
