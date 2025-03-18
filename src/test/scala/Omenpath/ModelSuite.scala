package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import org.scalajs.dom.Response
import com.raquo.laminar.api.L.{*, given}
import io.laminext.fetch.{Fetch, FetchResponse}

import scala.concurrent.*
import scala.concurrent.duration.*
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import org.scalamock.stubs.Stubs

import _root_.Omenpath.Func.*
import _root_.Omenpath.Data.*

class ModelSuite extends munit.FunSuite {
  test("rotateVector Test") {
    val v1: Vector[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8)
    val v2: Vector[Int] = Vector(23, 63, 46, 89, 34, 13)
    val v3: Vector[String] = Vector("apple", "banana", "carrot", "durian")
    assertEquals(rotateVector(v1, 5, 1), Vector(4, 5, 6, 7, 8, 1, 2, 3))
    assertEquals(rotateVector(v1, 2, 4), Vector(6, 7, 8, 1, 2, 3, 4, 5))
    assertEquals(rotateVector(v2, 63, 0), Vector(63, 46, 89, 34, 13, 23))
    assertEquals(rotateVector(v3, "apple", 1), Vector("durian", "apple", "banana", "carrot"))
  }

  test("String Assembly Test") {
    assertEquals("https://api.scryfall.com/cards/search?q=(game%3Apaper)+(s%3A" + ModernSets.takeRight(7).mkString("+or+s%3A") + ")", "https://api.scryfall.com/cards/search?q=(game%3Apaper)+(s%3ASOK+or+s%3ABOK+or+s%3ACHK+or+s%3A5DN+or+s%3ADST+or+s%3AMRD+or+s%3A8ED)")
    assertEquals("https://api.scryfall.com/cards/search?q=(game%3Apaper)+(s%3A" + rotateVector(ModernSets, "8ED", 2).takeRight(7).mkString("+or+s%3A") + ")", "https://api.scryfall.com/cards/search?q=(game%3Apaper)+(s%3AGPT+or+s%3ARAV+or+s%3A9ED+or+s%3ASOK+or+s%3ABOK+or+s%3ACHK+or+s%3A5DN)")
  }

  test("API Parse Test") {
    val mockResponseJson = js.Dynamic.literal(
      "object" -> "list",
      "total_cards" -> 604,
      "has_more" -> true,
      "next_page" -> "https://api.scryfall.com/cards/search?format=json&include_extras=false&include_multilingual=false&include_variations=false&order=name&page=2&q=c%3Awhite+mv%3D1&unique=cards",
      "data" -> js.Array(
        js.Dynamic.literal(
          "object" -> "card",
          "id" -> "023b5e6f-10de-422d-8431-11f1fdeca246",
          "name" -> "Abu Ja'far",
          "released_at" -> "1995-07-01",
          "image_uris" -> js.Dynamic.literal("small" -> "https://cards.scryfall.io/small/front/0/2/023b5e6f-10de-422d-8431-11f1fdeca246.jpg?1562895407"),
          "mana_cost" -> "{W}",
          "type_line" -> "Creature — Human",
          "rarity" -> "uncommon",
          "artist" -> "Ken Meyer, Jr."
        )
      )
    )
    val parsedResponse = parseScryfallListData(mockResponseJson)
  }
}

class ApiServiceTest extends munit.FunSuite with org.scalamock.stubs.Stubs {

  test("fetchData should return ApiResponse when fetch is successful") {
    // Step 1: Mock the Fetch response
    val url = "https://api.scryfall.com/cards/search?q=c%3Awhite+mv%3D1"
    val mockResponseJson = js.Dynamic.literal(
      "object" -> "list",
      "total_cards" -> 604,
      "has_more" -> true,
      "next_page" -> "https://api.scryfall.com/cards/search?format=json&include_extras=false&include_multilingual=false&include_variations=false&order=name&page=2&q=c%3Awhite+mv%3D1&unique=cards",
      "data" -> js.Array(
        js.Dynamic.literal(
          "object" -> "card",
          "id" -> "023b5e6f-10de-422d-8431-11f1fdeca246",
          "name" -> "Abu Ja'far",
          "released_at" -> "1995-07-01",
          "image_uris" -> js.Dynamic.literal("small" -> "https://cards.scryfall.io/small/front/0/2/023b5e6f-10de-422d-8431-11f1fdeca246.jpg?1562895407"),
          "mana_cost" -> "{W}",
          "type_line" -> "Creature — Human",
          "rarity" -> "uncommon",
          "artist" -> "Ken Meyer, Jr."
        )
      )
    )

    // Step 1.5: Create mock FetchResponse and return the mocked JSON
    val fakeFR = stub[FetchResponse]
    when(mockFetchResponse.json()).thenReturn(Future.successful(mockResponseJson))
/*
    // Step 2: Mock the Fetch.get method to return our mocked response
    val fetchMock = mock[Fetch.type]
    when(fetchMock.get(url)).thenReturn(mockFetchResponse)

    // Step 3: Call the API method
    val result = fetchData(url, scryfallFetchHeaders, parseScryfallListData)

    // Step 4: Assert the result in an asynchronous manner
    result.onComplete {
      case Success(ApiResponse(object_type, total_cards, has_more, next_page, warnings, data)) =>
        assertEquals(object_type, "list") // Ensure the data is correctly parsed
        assertEquals(total_cards, 604)
        assertEquals(has_more, true)
        assertEquals(next_page, "https://api.scryfall.com/cards/search?format=json&include_extras=false&include_multilingual=false&include_variations=false&order=name&page=2&q=c%3Awhite+mv%3D1&unique=cards")
        assertEquals(warnings, None)
        assertEquals(data, js.Array(
          js.Dynamic.literal(
            "object" -> "card",
            "id" -> "023b5e6f-10de-422d-8431-11f1fdeca246",
            "name" -> "Abu Ja'far",
            "released_at" -> "1995-07-01",
            "image_uris" -> js.Dynamic.literal("small" -> "https://cards.scryfall.io/small/front/0/2/023b5e6f-10de-422d-8431-11f1fdeca246.jpg?1562895407"),
            "mana_cost" -> "{W}",
            "type_line" -> "Creature — Human",
            "rarity" -> "uncommon",
            "artist" -> "Ken Meyer, Jr."
          )
        )
      )
      case Failure(exception) =>
        fail(s"Expected successful response but got failure: ${exception.getMessage}")
    }
  }

  test("fetchData should fail when fetch returns an error") {
    // Mock a failed network response
    val url = "https://example.com/api"
    val mockResponse = mock[FetchResponse]

    // Simulate a failure in the future (e.g., a network error)
    when(mockResponse.json()).thenReturn(Future.failed(new Exception("Network Error")))

    // Mock the Fetch.get method to return our mocked response
    val fetchMock = mock[Fetch.type]
    when(fetchMock.get(url)).thenReturn(mockResponse)

    // Call the API method
    val result = ApiService.fetchData(url)

    // Assert the result should fail
    result.onComplete {
      case Success(_) => fail("Expected failure but got success")
      case Failure(exception) =>
        assert(exception.getMessage == "Network Error") // Ensure the error is correctly handled
    }*/
  }

}