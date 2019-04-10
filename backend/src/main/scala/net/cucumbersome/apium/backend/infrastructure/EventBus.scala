package net.cucumbersome.apium.backend.infrastructure

import scalaz.zio.Task

trait EventBus[T] {
  def publishEvent(event: T): Task[Unit]
}
