package net.cucumbersome.apium.backend.people

sealed trait Event

case class PersonCreated(person: Person) extends Event