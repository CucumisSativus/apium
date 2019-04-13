package net.cucumbersome.apium.backend.people
import net.cucumbersome.apium.backend.people.PeopleEventBus.PeopleEventBusService
import scalaz.zio._

trait PeopleEventBus{
  def service: PeopleEventBusService
}
object PeopleEventBus{
  trait PeopleEventBusService{
    def publishEvent(event: Event): UIO[Unit]
  }

  val queueSize: Int = 100
  class LiveEventBusService(repository: PeopleRepositoryWrite) extends PeopleEventBusService {
    def publishEvent(event: Event): UIO[Unit] = event match {
      case PersonCreated(person) => repository.addPerson(person)
    }
  }
}
