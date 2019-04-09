package net.cucumbersome.apium.backend

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import net.cucumbersome.apium.backend
import net.cucumbersome.apium.backend.people.PeopleRepository
import net.cucumbersome.apium.backend.people.http.PeopleHttpService
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import scalaz.zio.interop.catz._
import scalaz.zio.interop.catz.implicits._
import scalaz.zio.{Task, ZIO}
import org.http4s.server.middleware._
import pureconfig.generic.auto._

object App extends CatsApp with StrictLogging{
  override def run(args: List[String]): ZIO[backend.App.Environment, Nothing, Int] = {
    val run = for{
      config <- Task(pureconfig.loadConfigOrThrow[ApplicationConfig](ConfigFactory.load()))
      repo =  PeopleRepository.apply(config.peopleRepository)
      _ <- startServer(repo)
    } yield ()

    run
      .either
      .map {
        case Left(ex) =>
          logger.error("Startup failed", ex)
          1
        case Right(_) =>
          0
      }

  }

  private def startServer(repo: PeopleRepository) = {
    val peopleService =  new PeopleHttpService(repo)
    val router = Router("/" -> CORS(peopleService.service)).orNotFound
    BlazeServerBuilder[Task]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(router)
      .resource
      .use(_ => Task.never)

  }
}

