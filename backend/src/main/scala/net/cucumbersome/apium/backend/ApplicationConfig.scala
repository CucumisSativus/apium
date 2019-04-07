package net.cucumbersome.apium.backend
import pureconfig.generic.auto._
import com.typesafe.config.Config
import net.cucumbersome.apium.backend.people.PeopleRepository
import pureconfig.error.ConfigReaderFailures

case class ApplicationConfig(
  peopleRepository: PeopleRepository.Configuration
)

object ApplicationConfig{

  def apply(config: Config): Either[ConfigReaderFailures, ApplicationConfig] =
    pureconfig.loadConfig[ApplicationConfig](config)
}