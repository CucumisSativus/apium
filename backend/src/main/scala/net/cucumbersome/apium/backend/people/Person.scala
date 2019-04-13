package net.cucumbersome.apium.backend.people
import shapeless.tag
import shapeless.tag.@@

case class Person(id: Person.Id, name: Person.Name)

object Person {
  sealed trait _NameTag
  sealed trait _IdTag

  type Name = String @@ _NameTag
  type Id = String @@ _IdTag

  trait PersonOps {
    implicit class ops(str: String) {
      def toName: Name = tag[_NameTag][String](str)
      def toId : Id = tag[_IdTag][String](str)
    }
  }
}
