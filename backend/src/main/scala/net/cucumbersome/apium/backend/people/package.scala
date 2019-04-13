package net.cucumbersome.apium.backend

import scalaz.zio.ZIO

package object people {
  def publishEvent(event: Event): ZIO[PeopleEventBus, Nothing, Unit] =
    ZIO.accessM(_.service.publishEvent(event))
}
