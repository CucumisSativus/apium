package net.cucumbersome.apium.backend.people.http
import cats.Applicative
import cats.effect.Effect
import cats.implicits._
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.refined.refinedEncoder
import net.cucumbersome.apium.backend.people.PeopleRepository
import net.cucumbersome.apium.backend.people.Person._NameTag
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import shapeless.tag.@@
class PeopleHttpService[F[_]: Applicative: Effect](repo: PeopleRepository[F])
    extends Http4sDsl[F] {
  import PeopleHttpService._
  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "people" =>
        repo.getAll.flatMap(p => Ok(p.asJson))
    }
  }
}

object PeopleHttpService{
  implicit val nameEncoder : Encoder[String @@ _NameTag] = refinedEncoder
}
