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

import _root_.Omenpath.Data.*

// Type alias
type SetList = Vector[String]

// Case classes for detailed API Response Structure
trait ApiData
case class ScryfallApiListData(object_type: String, total_cards: Int, has_more: Boolean, next_page: String, warnings: Array[String], data: Any) extends ApiData

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

final class Model: // Data Model for communicating data to front end.
  import Func.{getRotation, rotateVector}

  // Mutable variables and signals.

  val setData: Var[SetList] = Var(Vector())           // For communicating a list of sets.
  val setDataSignal = setData.signal

  val currentSlot: Var[Int] = Var(0)                  // For tracking which slots are spinning and which are stopped.
  val currentSlotSignal = currentSlot.signal

  val currentDisplay: Var[Int] = Var(0)               // For tracking which set code displays are active.
  val currentDisplaySignal = currentDisplay.signal

  val slotContainerClass: Var[String] = Var("hidden") // For unhiding the slots.
  val slotContainerClassSignal = slotContainerClass.signal

  val btnClass: Var[String] = Var("")                 // For hiding the button.
  val btnClassSignal = btnClass.signal

  // Front-end & update functions

  def rollSets(newSeason: Int): Unit = {
    setData.update(_ => 
      (getRotation(ModernSets, SeasonRecord("Season " + (newSeason-1)) ++ SeasonRecord("Season " + (newSeason-2)))))
    startSlots()
  }
  def startSlots(): Unit = {
    btnClass.update(_ => "hidden")
    slotContainerClass.update(_ => "")
  }
  
end Model

object Func: // Backend Functions
  // Takes a vector and moves some number of elements from the front to the back or vice versa to move the pivot element (first instance of) to the specified position (index).
  def rotateVector[A](v: Vector[A], pivot: A, pos: Int): Vector[A] = {
    if (!v.contains(pivot)) throw new Error("Pivot element not present in vector.")
    else
      val pivotPoint = v.indexOf(pivot)   // Find current index of pivot
      if (pivotPoint == pos) v            // If the pivot is already at the goal index, return the vector unchanged.
      else if (pivotPoint < pos) {        // If the pivot is before the index, move a number of elements from the end of the vector to the front.
        val splitV = v.splitAt(v.length - (pos - pivotPoint))
        splitV._2 ++ splitV._1
      } else {                            // If the pivot is after the index, move a number of elements from the front of the vector to the end.
        val splitV = v.splitAt(pivotPoint - pos)
        splitV._2 ++ splitV._1
      }
  }
  // Makes an API fetch request.
  def fetchData[T <: ApiData](
      url: String, headers: Map[String, String], 
      parseData: js.Dynamic => T,
      fetcher: (String, Map[String, String]) => Future[FetchResponse[scala.scalajs.js.Any]] = Fetch.get(_, _).future.json()
    ): Future[T] = {
    fetcher(url, headers).flatMap { response =>
      if (response.ok) {
        // Successfully fetched the data, now attempt to parse it.
          Future(parseData(response.data.asInstanceOf[js.Dynamic])).recoverWith { e => Future.failed(new Exception("Error parsing API response", e)) }
      } else {
        // Handle error response.
        Future.failed(new Exception("Failed to fetch API data"))
      }
    }
  }
  // Parses a response of type List from the Scryfall api.
  def parseScryfallListData(json: js.Dynamic): ScryfallApiListData = {
    ScryfallApiListData(
      object_type = json.`object`.toString,
      total_cards = json.total_cards.asInstanceOf[js.UndefOr[Int]].getOrElse(-1),
      has_more = json.has_more.asInstanceOf[Boolean],
      next_page = json.next_page.asInstanceOf[js.UndefOr[String]].getOrElse(""),
      warnings = json.warnings.asInstanceOf[js.UndefOr[Array[String]]].getOrElse(Array[String]()),
      data = json.data
    )
  }
  // Creates a list of legal sets for the new rotation from the list of all possible sets and the list of locked out sets.
  def getRotation(legalSets: SetList, lockOut: SetList) = {
    val possibleSets = legalSets.diff(lockOut)                  // Remove the "locked out" sets (from previous rotations) from the list of available sets.
    val randomSets = scala.util.Random.shuffle(possibleSets)    // Shuffle the list of sets to get a random selection.
    coreSetCheck(randomSets).take(7)                            // Remove any core sets after the first two.
  }
  // Takes a list of sets and removes any core sets appearing after the first two.
  def coreSetCheck(sets: SetList): SetList = {
    val coreSetsFromShuffledList = sets.filter(Data.CoreSets.contains(_))    // Filter the list to only the core sets, preserving their order.
    sets.diff(coreSetsFromShuffledList.drop(2))                         // Remove all but the first two core sets from the list.
  }
  // Takes a list of sets and returns the scryfall query string for the url.
  def getScryfallQuery(sets: SetList): String = {
    "q=(game%3Apaper)+(s%3A" + sets.mkString("+or+s%3A") + ")"
  }
  // Returns a SetList that meets the minimum card count, starting from the minimum set count and incrementing by 1.
  def requiredCount(sets: SetList, setCount: Int): SetList = {
    val queryURL = "https://api.scryfall.com/cards/search?" + getScryfallQuery(sets.take(setCount))
    val response = fetchData(queryURL, scryfallFetchHeaders, parseScryfallListData).delay(Duration(100, "millis"))
    response.onComplete {
      case Success(ScryfallApiListData(object_type, total_cards, has_more, next_page, warnings, data)) => 
        if (total_cards >= minCardCount) {sets.take(setCount)}
        else {requiredCount(sets, setCount + 1)}
      case Failure(exception) => handleAPIFailure(exception, sets.take(setCount))
    }
  }

  def handleAPIFailure[A](exception: Throwable, return_val: A) = {return_val}

end Func