package net.cucumbersome.apium.backend.people
import shapeless.tag
import shapeless.tag.@@

case class Person(name: Person.Name)

object Person{
  sealed trait _NameTag

  type Name = String @@ _NameTag

  trait PersonOps{
    implicit class ops(str: String){
      def toName: Name = tag[_NameTag][String](str)
    }
  }
}

