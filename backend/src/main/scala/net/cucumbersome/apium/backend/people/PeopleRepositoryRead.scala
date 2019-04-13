package net.cucumbersome.apium.backend.people

import java.util.UUID

import net.cucumbersome.apium.backend.people.Person.PersonOps
import scalaz.zio.UIO
trait PeopleRepositoryRead {
  def getAll: UIO[List[Person]]
}

trait PeopleRepositoryWrite{
  def addPerson(person: Person): UIO[Unit]
}
object PeopleRepositoryRead extends PersonOps {
  def apply(
    configuration: Configuration): PeopleRepositoryRead = {
    new PeopleRepositoryRead with PeopleRepositoryWrite {
      var people : List[Person] = buildFakePeople(configuration.peopleNumber)
      override def getAll: UIO[List[Person]] =
        UIO.apply(people)

      override def addPerson(person: Person): UIO[Unit] = UIO{
        people = people :+ person
      }
    }
  }

  private def buildFakePeople(amount: Int): List[Person] = {
    (0 until amount).map(i =>
      Person(
        id = UUID.randomUUID().toString.toId,
        name = s"name $i".toName
      )
    ).toList
  }

  case class Configuration(
    peopleNumber: Int
  )

}
