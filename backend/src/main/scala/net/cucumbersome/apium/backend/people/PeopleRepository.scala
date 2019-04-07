package net.cucumbersome.apium.backend.people

import net.cucumbersome.apium.backend.people.Person.PersonOps
import scalaz.zio.Task
trait PeopleRepository {
  def getAll: Task[Seq[Person]]
}

object PeopleRepository extends PersonOps {
  def apply(
    configuration: Configuration): PeopleRepository = {
    new PeopleRepository {
      override def getAll: Task[Seq[Person]] =
        Task.apply(buildFakePeople(configuration.peopleNumber))
    }
  }

  private def buildFakePeople(amount: Int): Seq[Person] = {
    (0 until amount).map(i =>
      Person(
        name = s"name $i".toName
      )
    )
  }

  case class Configuration(
    peopleNumber: Int
  )

}
