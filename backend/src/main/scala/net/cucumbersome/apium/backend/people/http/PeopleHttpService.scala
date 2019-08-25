package net.cucumbersome.apium.backend.people.http
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.refined.refinedEncoder
import io.circe.syntax._
import net.cucumbersome.apium.backend.people.PeopleCommandHandler.CreatePerson
import net.cucumbersome.apium.backend.people.PeopleRepositoryRead
import net.cucumbersome.apium.backend.people.Person.{_IdTag, _NameTag}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import zio.Task
import zio.interop.catz._
import shapeless.tag.@@

class PeopleHttpService(repo: PeopleRepositoryRead, commandHandler: CommandHandlerAdapter)
    extends Http4sDsl[Task] {
  import PeopleHttpService._
  implicit val createPerson = jsonOf[Task, CreatePerson]
  val service: HttpRoutes[Task] = {
    HttpRoutes.of[Task] {
      case GET -> Root / "people" =>
        repo.getAll.flatMap(p => Ok(p.asJson))
      case req @ POST -> Root / "people" =>
        for {
          command <- req.as[CreatePerson]
          res <- createPerson(command)
        } yield res

    }
  }

  private def createPerson(command: CreatePerson): Task[Response[Task]] = {
    commandHandler.handle(command).either.flatMap{
      case Right(_) => Created()
      case Left(_) => BadRequest()
    }
  }
}

object PeopleHttpService{
  implicit val nameEncoder : Encoder[String @@ _NameTag] = refinedEncoder
  implicit val idEncoder : Encoder[String @@ _IdTag] = refinedEncoder
}
