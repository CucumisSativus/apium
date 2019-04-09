package net.cucumbersome.apium.backend

import net.cucumbersome.apium.backend.people.PeopleRepository

case class ApplicationConfig(
  peopleRepository: PeopleRepository.Configuration
)

