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
import scala.util.{Failure, Success}

import org.scalamock.stubs.Stubs

import _root_.Omenpath.Func.*
import _root_.Omenpath.Data.*
import org.scalajs.dom.Headers
import scala.annotation.experimental
import scala.annotation.switch

extension [A](obj: Future[A]) {
  /** Returns a [[Future]] that completes after the specified duration. */
  def delay(duration: FiniteDuration): Future[A] = {
    val promise = scala.concurrent.Promise[Unit]()
    obj.transformWith { result =>
      val _ = scalajs.js.timers.setTimeout(duration) {
        promise.success(())
      }
      promise.future.flatMap(_ => Future.fromTry(result))
    }
  }
}

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
    assertEquals(parsedResponse.object_type, "list")
    assertEquals(parsedResponse.total_cards, 604)
    assertEquals(parsedResponse.has_more, true)
    assertEquals(parsedResponse.next_page, "https://api.scryfall.com/cards/search?format=json&include_extras=false&include_multilingual=false&include_variations=false&order=name&page=2&q=c%3Awhite+mv%3D1&unique=cards")
    assertEquals(parsedResponse.warnings.toSeq, Array[String]().toSeq)
  }
}

class ApiServiceTest extends munit.FunSuite {
  // Step 1: Mock the Fetch response
  val testUrl = "https://api.scryfall.com/cards/search?q=c%3Awhite+mv%3D1"
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
// Step 2: Create mock fetcher which returns a mock response on correct inputs, and various failed results for improper inputs, after a delay
  def fakeFetcher(url: String, headers: Map[String, String]): Future[FetchResponse[scala.scalajs.js.Any]] = {
    val d500m = Duration(500, "millis")
    if (url == testUrl && headers == scryfallFetchHeaders) {
      Future.successful(new FetchResponse(true, 200, "sampletext", new Headers(), org.scalajs.dom.ResponseType.default, mockResponseJson, testUrl)).delay(d500m)
    } else if (url == testUrl) {
      Future.successful(new FetchResponse(false, 400, "sampletext", new Headers(), org.scalajs.dom.ResponseType.error, mockResponseJson, testUrl)).delay(d500m)
    } else if (headers == scryfallFetchHeaders) {
      Future.successful(new FetchResponse(true, 200, "sampletext", new Headers(), org.scalajs.dom.ResponseType.default, js.Dynamic.literal("object" -> "card", "name" -> "Abu Ja'far", "mana_cost" -> "{W}"), testUrl)).delay(d500m)
    } else {
      Future.failed(new Exception("Network Error"))
    }
  }

  test("fetchData should return ApiResponse when fetch is successful") { Future {
    // Step 3: Call the API method
    
    val result = fetchData(testUrl, scryfallFetchHeaders, parseScryfallListData, fakeFetcher)

    // Step 4: Assert the result in an asynchronous manner
    result.onComplete {
      case Success(ScryfallApiListData(object_type, total_cards, has_more, next_page, warnings, data)) =>
        assertEquals(object_type, "list") // Ensure the data is correctly parsed
        assertEquals(total_cards, 604)
        assertEquals(has_more, true)
        assertEquals(next_page, "https://api.scryfall.com/cards/search?format=json&include_extras=false&include_multilingual=false&include_variations=false&order=name&page=2&q=c%3Awhite+mv%3D1&unique=cards")
        assertEquals(warnings.toSeq, Array[String]().toSeq)
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
  }}

  // Step 5: Call the API method improperly and assert the failed results.

  test("fetchData should fail when fetch returns an error") { Future {
    val result = fetchData(testUrl, Map("bad" -> "header"), parseScryfallListData, fakeFetcher)

    result.onComplete {
      case Success(_) => fail("Expected failure but got success")
      case Failure(exception) =>
        assert(exception.getMessage == "Failed to fetch API data") // Ensure the error is correctly handled
    }
  }}

  test("fetchData should fail when fetch returns unexpected data") { Future {
    val result = fetchData("wrong.url", scryfallFetchHeaders, parseScryfallListData, fakeFetcher)

    result.onComplete {
      case Success(_) => fail("Expected failure but got success")
      case Failure(exception) =>
        assert(exception.getMessage == "Error parsing API response") // Ensure the error is correctly handled
    }
  }}

  test("fetchData should fail when fetch doesn't return") { Future {
    val result = fetchData("bad.url", Map("bad" -> "header"), parseScryfallListData, fakeFetcher)

    result.onComplete {
      case Success(_) => fail("Expected failure but got success")
      case Failure(exception) =>
        assert(exception.getMessage == "Network Error") // Ensure the error is correctly handled
    }
  }}

}