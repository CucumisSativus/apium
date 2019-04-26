module ListUtilsTest exposing (..)

import Expect exposing (Expectation)
import Fuzz exposing (Fuzzer, int, list, string)
import Test exposing (..)
import ListUtils exposing(inGropsOf)

suite : Test
suite =
  describe "in groups of"
    [ test "group of empty list " <|
      \_ -> inGropsOf 3 []
        |> Expect.equal []
      , test "group matching size" <|
        \_ -> inGropsOf 3 [1, 2, 3]
        |> Expect.equal [[1,2,3]]
      , test "multiple groups matching size" <|
        \_ -> inGropsOf 3 [1,2,3,4,5,6]
        |> Expect.equal [[1,2,3], [4,5,6]]
      , test "multiple groups unequal size" <|
        \_ -> inGropsOf 3 [1,2,3,4,5]
        |> Expect.equal [[1,2,3], [4,5]]      
    ]
