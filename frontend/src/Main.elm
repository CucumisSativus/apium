module Main exposing (main)

import Bootstrap.CDN as CDN
import Bootstrap.Grid as Grid
import Bootstrap.Grid.Row as Row
import Bootstrap.Grid.Col as Col
import Browser exposing (..)
import Html exposing (..)
import Html.Attributes exposing(..)
import ListUtils exposing(inGropsOf)
import Json.Decode exposing (Decoder, map2, field, string)
import Http

main =
    Browser.element { init = initialize, update = update, view = view, subscriptions = subscriptions }


type Model =
    Loading |
    LoadedPeople { people : List Person } |
    ErrorLoading String


type alias Person =
    { name : String
    }


initialize : () -> (Model, Cmd Message)
initialize _ = (Loading, getPeople)
    -- LoadedPeople { people = [ {name = "no elo"}] }


subscriptions : Model -> Sub Message
subscriptions model =
  Sub.none

type Message
    = PeopleLoaded (Result Http.Error (List Person))



update : Message -> Model -> (Model, Cmd Message)
update message model =
    case message of
        PeopleLoaded result ->
          case result of
            Ok people ->
              (appendPeople model people, Cmd.none)
            Err err ->
              Debug.log (Debug.toString err)
              (ErrorLoading (Debug.toString err), Cmd.none)

appendPeople: Model -> List Person -> Model
appendPeople model newPeople =
  case model of
    LoadedPeople m ->
      LoadedPeople{ m | people = m.people ++ newPeople }
    _ ->
        LoadedPeople{ people = newPeople }

view : Model -> Html Message
view model =
  let
    content = case model of
      LoadedPeople{people} -> renderPeople people
      Loading -> renderLoading
      ErrorLoading err -> renderLoadingError err
  in
    Grid.container []
        [ CDN.stylesheet -- creates an inline style node with the Bootstrap CSS
        , div []
          [ content ]
        ]

renderLoadingError: String -> Html Message
renderLoadingError error =
    div [ class "row" ]
      [
      div [ class "col-md"]
      [ div [ class "text-primary", attribute "role" "status" ]
          [ p [  ]
              [ text ("Error while loading "  ++ error) ]
          ]
      ]
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

personDecoder: Decoder (List Person)
personDecoder = Json.Decode.list (Json.Decode.map Person (field "name" string))

getPeople: Cmd Message
getPeople =
  Http.get
      { url = "http://localhost:8080/people"
      , expect = Http.expectJson PeopleLoaded personDecoder
      }
