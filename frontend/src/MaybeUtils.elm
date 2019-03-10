module MaybeUtils exposing(getOrElse)

getOrElse : Maybe a -> a -> a
getOrElse opt orElse =
  case opt of
    Just a -> a
    Nothing -> orElse
