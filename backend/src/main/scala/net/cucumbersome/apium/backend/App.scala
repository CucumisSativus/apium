package net.cucumbersome.apium.backend

import com.typesafe.config.ConfigFactory
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

object App extends CatsApp {

  val appConfig = ApplicationConfig(ConfigFactory.load())
    .fold(er => sys.error(s"Error loading config $er"), identity)

  private def peopleService = new PeopleHttpService(
    PeopleRepository.apply(appConfig.peopleRepository)
  )

  override def run(args: List[String]): ZIO[backend.App.Environment, Nothing, Int] = {
    val router = Router("/" -> CORS(peopleService.service)).orNotFound
    BlazeServerBuilder[Task]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(router)
      .resource
      .use(_ => Task.never)
      .either
      .map{
        case Left(_) => 1
        case Right(_) => 0
      }

  }
}

