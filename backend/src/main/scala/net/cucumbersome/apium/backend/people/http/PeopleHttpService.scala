package net.cucumbersome.apium.backend.people.http
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.refined.refinedEncoder
import io.circe.syntax._
import net.cucumbersome.apium.backend.people.PeopleRepositoryRead
import net.cucumbersome.apium.backend.people.Person._NameTag
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.Task
import shapeless.tag.@@
import scalaz.zio.interop.catz._

class PeopleHttpService(repo: PeopleRepositoryRead)
    extends Http4sDsl[Task] {
  import PeopleHttpService._
  val service: HttpRoutes[Task] = {
    HttpRoutes.of[Task] {
      case GET -> Root / "people" =>
        repo.getAll.flatMap(p => Ok(p.asJson))
    }
  }
}

object PeopleHttpService{
  implicit val nameEncoder : Encoder[String @@ _NameTag] = refinedEncoder
}
