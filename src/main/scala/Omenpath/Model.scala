package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

import _root_.Omenpath.Data.*

type SetList = Vector[String]

final class Model: // Data Model for communicating data to front end.
  import Func.getRotation

  // Mutable variables and signals.

  val setData: Var[SetList] = Var(Vector()) // For communicating a list of sets.
  val setDataSignal = setData.signal

  val currentSlot: Var[Int] = Var(0)
  val currentSlotSignal = currentSlot.signal

  val slotContainerClass: Var[String] = Var("slot-main hidden") // For unhiding the slots.
  val slotContainerClassSignal = slotContainerClass.signal

  val slotClassList: Vector[Var[String]] = Vector( // For manipulating class assignments for the slots.
    Var("slot loop loop1"),
    Var("slot loop loop2"),
    Var("slot loop loop3"),
    Var("slot loop loop4"),
    Var("slot loop loop5"),
    Var("slot loop loop6"),
    Var("slot loop loop7")
  )

  val btnClass: Var[String] = Var("") // For hiding the button.
  val btnClassSignal = btnClass.signal

  // Front-end & update functions

  def rollSets(newSeason: Int): Unit = {
    setData.update(_ => 
      (getRotation(ModernSets, SeasonRecord("Season " + (newSeason-1)) ++ SeasonRecord("Season " + (newSeason-2)))))
    btnClass.update(_ => "hidden")
    slotContainerClass.update(_ => "slot-main")
  }

  def getSlotClasses(index: Int): Signal[String] = {
    currentSlotSignal.map( _: Int =>
      if (index < _) ("slot loop loop" + index)
      else "slot stop"
    )
  }

  def progressSlots(): Unit = {
    currentSlotSignal.map(
      slotClassList(_).update{_ => "slot stop"}
    )
    currentSlot.update(_ + 1)
  }
  
end Model

object Func: // Backend Functions
  // Takes the list of legal sets and the list of sets from the previous rotation and returns a list of 7 sets legal in the new rotation.
  def getRotation(legalSets: SetList, lockOut: SetList) =
    val possibleSets = legalSets.diff(lockOut)               // Remove the "locked out" sets (from the previous two rotations) from the list of available sets.
    val randomSets = scala.util.Random.shuffle(possibleSets)    // Shuffle the list of sets to get a random selection.
    coreSetCheck(randomSets).take(7)                            // Remove any core sets after the first two, then take the first 7 sets.

  // Takes a list of sets and removes any core sets appearing after the first two.
  def coreSetCheck(sets: SetList): SetList =
    val coreSetsFromShuffledList = sets.filter(Data.CoreSets.contains(_))    // Filter the list to only the core sets, preserving their order.
    sets.diff(coreSetsFromShuffledList.drop(2))                         // Remove all but the first two core sets from the list.