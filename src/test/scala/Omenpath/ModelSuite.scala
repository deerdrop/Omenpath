package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import org.scalajs.dom.Response
import com.raquo.laminar.api.L.{*, given}
import io.laminext.fetch.{Fetch, FetchResponse}

import scala.concurrent.*
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

import _root_.Omenpath.Func.rotateVector
import _root_.Omenpath.Data.ModernSets

class ModelSuite extends munit.FunSuite {
  test("basic") {

  }

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

  test("API Call Test") {
    val response: Future[FetchResponse[dom.Blob]] = Fetch.get("https://api.scryfall.com/cards/search?q=(game%3Apaper)+(s%3ASOK+or+s%3ABOK+or+s%3ACHK+or+s%3A5DN+or+s%3ADST+or+s%3AMRD+or+s%3A8ED)", headers = Map("User-Agent" -> "OmenpathSetApp/0.1", "Accept" -> "*/*")).future.blob()
    Await.ready(response, 10.seconds)
    //assertEquals(true, response.map{ resp => resp.data})
  }
}