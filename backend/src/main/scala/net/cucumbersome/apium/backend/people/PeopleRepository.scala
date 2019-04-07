package net.cucumbersome.apium.backend.people

import cats.Applicative
import net.cucumbersome.apium.backend.people.Person.PersonOps
trait PeopleRepository[F[_]] {
  def getAll: F[Seq[Person]]
}

object PeopleRepository extends PersonOps {
  def apply[F[_]: Applicative](
    configuration: Configuration): PeopleRepository[F] = {
    val F = implicitly[Applicative[F]]
    new PeopleRepository[F] {
      override def getAll: F[Seq[Person]] =
        F.pure(buildFakePeople(configuration.peopleNumber))
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
