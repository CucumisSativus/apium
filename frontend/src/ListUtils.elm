module ListUtils exposing(inGropsOf)
import MaybeUtils exposing(getOrElse)



inGropsOf : Int -> List a -> List(List a)
inGropsOf groupSize elements =
  ingroupsOfIterate groupSize elements 1 []

init: List a -> List a
init la =
  case List.reverse la of
    [] -> []
    h :: t -> List.reverse t

last : List a -> Maybe a
last la =
  case List.reverse la of
    [] -> Nothing
    h :: t -> Just h

-- Private
ingroupsOfIterate : Int -> List a -> Int -> List(List a) -> List(List a)
ingroupsOfIterate groupSize notGrouped iterator acc =
  case notGrouped of
    [] -> acc
    h :: t ->
      if  modBy iterator groupSize == 0 then
          ingroupsOfIterate groupSize t (iterator+1) acc ++ [[h]]
      else
        let
          lastEl = last acc
          listToAppend = (getOrElse lastEl []) ++ [h]
          listToPrepend = init acc
        in ingroupsOfIterate groupSize t (iterator+1) (listToPrepend ++ [listToAppend])
