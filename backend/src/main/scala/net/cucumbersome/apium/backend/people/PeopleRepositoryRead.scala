package net.cucumbersome.apium.backend.people

import net.cucumbersome.apium.backend.people.Person.PersonOps
import scalaz.zio.Task
trait PeopleRepositoryRead {
  def getAll: Task[List[Person]]
}

trait PeopleRepositoryWrite{
  def addPerson(person: Person): Task[Unit]
}
object PeopleRepositoryRead extends PersonOps {
  def apply(
    configuration: Configuration): PeopleRepositoryRead = {
    new PeopleRepositoryRead with PeopleRepositoryWrite {
      var people : List[Person] = buildFakePeople(configuration.peopleNumber)
      override def getAll: Task[List[Person]] =
        Task.apply(people)

      override def addPerson(person: Person): Task[Unit] = Task{
        people = people :+ person
      }
    }
  }

  private def buildFakePeople(amount: Int): List[Person] = {
    (0 until amount).map(i =>
      Person(
        name = s"name $i".toName
      )
    ).toList
  }

  case class Configuration(
    peopleNumber: Int
  )

}
