package net.cucumbersome.apium.backend.people
import net.cucumbersome.apium.backend.infrastructure.EventBus
import scalaz.zio._

class PeopleEventBus(repository: PeopleRepositoryWrite) extends EventBus[Event]{
//  import PeopleEventBus._
//  val bus = Ref[Queue[Event]](Queue.bounded(queueSize))

  override def publishEvent(event: Event): Task[Unit] = event match {
    case PersonCreated(person) => repository.addPerson(person)
  }
}

object PeopleEventBus{
  val queueSize: Int = 100
}
