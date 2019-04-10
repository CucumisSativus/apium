package net.cucumbersome.apium.backend

import net.cucumbersome.apium.backend.people.PeopleRepositoryRead

case class ApplicationConfig(
  peopleRepository: PeopleRepositoryRead.Configuration
)

