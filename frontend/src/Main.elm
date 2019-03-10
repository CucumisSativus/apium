module Main exposing (main)

import Bootstrap.CDN as CDN
import Bootstrap.Grid as Grid
import Bootstrap.Grid.Row as Row
import Bootstrap.Grid.Col as Col
import Browser exposing (..)
import Html exposing (..)
import Html.Attributes exposing(..)
import ListUtils exposing(inGropsOf)
main =
    Browser.sandbox { init = initialize, update = update, view = view }


type Model =
    Loading |
    LoadedPeople { people : List Person }


type alias Person =
    { name : String
    }


initialize : Model
initialize = Loading
    -- LoadedPeople { people = [ {name = "no elo"}] }


type Message
    = AddPerson Person


update : Message -> Model -> Model
update message model =
    case (message, model) of
        (AddPerson newPerson, LoadedPeople m ) ->
          LoadedPeople{ m | people = m.people ++ [ newPerson ] }
        (AddPerson newPerson, _) ->
          LoadedPeople{ people = [ newPerson ] }


view : Model -> Html Message
view model =
  let
    content = case model of
      LoadedPeople{people} -> renderPeople people
      Loading -> renderLoading
  in
    Grid.container []
        [ CDN.stylesheet -- creates an inline style node with the Bootstrap CSS
        , div []
          [ content ]
        ]

renderLoading : Html Message
renderLoading =
    div [ class "row" ]
      [
      div [ class "col-md"]
      [ div [ class "spinner-border text-primary", attribute "role" "status" ]
          [ span [ class "sr-only" ]
              [ text "Loading..." ]
          ]
      ]
    ]
renderPeople : List Person -> Html Message
renderPeople personList =
    let
      groupedPeople = inGropsOf 4 personList
      renderedPeople = List.map renderPersonRow groupedPeople
    in div [] renderedPeople

renderPersonRow : List Person -> Html Message
renderPersonRow personList =
    let
        renderedGuys = List.map (renderPerson >> wrapIntoColumn) personList
    in
      div [ class "row" ]
        renderedGuys

wrapIntoColumn : Html Message -> Html Message
wrapIntoColumn messageHtml =
     div [ class "col-md"]
      [messageHtml]

renderPerson : Person -> Html Message
renderPerson person =
    div [ class "card", attribute "style" "width: 18rem;" ]
      [ img [ alt "...", class "card-img-top", src "..." ]
          []
      , div [ class "card-body" ]
          [ h5 [ class "card-title" ]
              [ text person.name ]
          , p [ class "card-text" ]
              [ text "Some quick example text to build on the card title and make up the bulk of the card's content." ]
          , a [ class "btn btn-primary", href "#" ]
              [ text "Go somewhere" ]
          ]
      ]
