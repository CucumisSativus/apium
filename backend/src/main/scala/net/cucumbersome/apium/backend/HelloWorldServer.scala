package net.cucumbersome.apium.backend

import cats.effect.{Effect, IO}
import com.typesafe.config.ConfigFactory
import fs2.StreamApp
import net.cucumbersome.apium.backend.people.PeopleRepository
import net.cucumbersome.apium.backend.people.http.PeopleHttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  val appConfig = ApplicationConfig(ConfigFactory.load())
    .fold(er => sys.error(s"Error loading config $er"), identity)

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    new ServerStream[IO](appConfig).stream
}

class ServerStream[F[_]: Effect](config: ApplicationConfig){
  private def peopleService = new PeopleHttpService[F](
    PeopleRepository.apply(config.peopleRepository)
  )
  def stream(implicit ec: ExecutionContext) =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(peopleService.service, "/")
      .serve
}
