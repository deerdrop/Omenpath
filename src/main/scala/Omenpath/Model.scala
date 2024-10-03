package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

import _root_.Omenpath.Data.*

type SetList = Vector[String]

final class Model: // Data Model for communicating data to front end.
  import Func.{getRotation, rotateVector}

  // Mutable variables and signals.

  val setData: Var[SetList] = Var(Vector()) // For communicating a list of sets.
  val setDataSignal = setData.signal

  val currentSlot: Var[Int] = Var(0)
  val currentSlotSignal = currentSlot.signal

  val slotContainerClass: Var[String] = Var("slot-main hidden") // For unhiding the slots.
  val slotContainerClassSignal = slotContainerClass.signal

  val btnClass: Var[String] = Var("") // For hiding the button.
  val btnClassSignal = btnClass.signal

  // Front-end & update functions

  def rollSets(newSeason: Int): Unit = {
    setData.update(_ => 
      (getRotation(ModernSets, SeasonRecord("Season " + (newSeason-1)) ++ SeasonRecord("Season " + (newSeason-2)))))
    btnClass.update(_ => "hidden")
    slotContainerClass.update(_ => "slot-main")
  }
  
end Model

object Func: // Backend Functions
  // Takes the list of legal sets and the list of sets from the previous rotation and returns a list of 7 sets legal in the new rotation.
  def getRotation(legalSets: SetList, lockOut: SetList) = {
    val possibleSets = legalSets.diff(lockOut)               // Remove the "locked out" sets (from the previous two rotations) from the list of available sets.
    val randomSets = scala.util.Random.shuffle(possibleSets)    // Shuffle the list of sets to get a random selection.
    coreSetCheck(randomSets).take(7)                            // Remove any core sets after the first two, then take the first 7 sets.
  }
  // Takes a list of sets and removes any core sets appearing after the first two.
  def coreSetCheck(sets: SetList): SetList = {
    val coreSetsFromShuffledList = sets.filter(Data.CoreSets.contains(_))    // Filter the list to only the core sets, preserving their order.
    sets.diff(coreSetsFromShuffledList.drop(2))                         // Remove all but the first two core sets from the list.
  }
  
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

end Func