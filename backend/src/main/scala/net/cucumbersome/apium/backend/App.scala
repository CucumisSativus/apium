package net.cucumbersome.apium.backend

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import net.cucumbersome.apium.backend
import net.cucumbersome.apium.backend.people.http.{CommandHandlerAdapter, PeopleHttpService}
import net.cucumbersome.apium.backend.people.{PeopleCommandHandler, PeopleEventBus, PeopleRepositoryRead}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{Task, ZIO}
import pureconfig.generic.auto._

object App extends CatsApp with StrictLogging{
  override def run(args: List[String]): ZIO[backend.App.Environment, Nothing, Int] = {
    val run = for{
      config <- Task(pureconfig.loadConfigOrThrow[ApplicationConfig](ConfigFactory.load()))
      repo =  PeopleRepositoryRead.apply(config.peopleRepository)
      commandHandler = PeopleCommandHandler.apply
      eventBus = PeopleEventBus.apply(repo)
      _ <- startServer(repo, commandHandler, eventBus)
    } yield ()

    run
      .either
      .map {
        case Left(ex) =>
          logger.error("Startup failed", ex)
          1
        case Right(_) => 0
      }

  }

  private def startServer(repo: PeopleRepositoryRead, commandHandler: PeopleCommandHandler, eventBus: PeopleEventBus) = {
    val peopleService =  new PeopleHttpService(repo, CommandHandlerAdapter(commandHandler, eventBus))
    val router = Router("/" -> CORS(peopleService.service)).orNotFound
    BlazeServerBuilder[Task]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(router)
      .resource
      .use(_ => Task.never)

  }
}

