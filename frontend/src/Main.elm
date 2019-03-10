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


type alias Model =
    { people : List Person
    }


type alias Person =
    { name : String
    }


initialize : Model
initialize =
    { people = [ {name = "no elo"}] }


type Message
    = AddPerson Person


update : Message -> Model -> Model
update message model =
    case message of
        AddPerson newPerson ->
            { model | people = model.people ++ [ newPerson ] }


view : Model -> Html Message
view model =
  let
    groupedPeople = inGropsOf 4 model.people
    renderedPeople = List.map renderPersonRow groupedPeople
  in
    Grid.container []
        [ CDN.stylesheet -- creates an inline style node with the Bootstrap CSS
        , div [] renderedPeople
        ]

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
