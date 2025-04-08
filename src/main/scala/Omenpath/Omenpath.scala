package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import _root_.Omenpath.Func.rotateVector

@main
def Omenpath(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
}

object Main:
  val model = new Model
  import model.*

  def appElement(): Element = {
    div(
      h1(idAttr := "title", "Omenpath Season " + Data.newSeason + " Rotation"),
      div( cls := "slots-main",             // Container for all the slots with transparent bg
        cls <-- slotContainerClassSignal,   // Signal to have container hidden at start
        children <-- setDataSignal.splitByIndex (renderSlot)
      ),
      button("Open the Paths", cls <-- btnClassSignal, onClick --> (_ => rollSets(Data.newSeason))),
    )
  }

  def renderSlot(index: Int, initialSetCode: String, setCodeSignal: Signal[String]): Element = {
    div( cls := "slot-section",                              // Each section contains the slot itself, and a display
      div ( cls := "display dotmatrix",
        p( initialSetCode,
          cls <-- currentDisplaySignal.map { (current: Int) =>
          if (index >= current) "hidden" else "" },          // Display begins empty, then turns on when its index is reached
        )
      ),
      div( cls := "slot",
        cls <-- currentSlotSignal.map{ (current: Int) =>
          if (index >= current) "loop loop" + index else "stop" },    // Slot begins in loop animation, then stops when its index is reached
        div(cls := "slot-inner",
          renderWheel(initialSetCode)
        )
      ),
      button(cls := "round",
        disabled <-- currentSlotSignal.combineWithFn(currentDisplaySignal)(_ != index || _ != index),  // Button is enabled only if both its slot's index and its display's index have been reached.
        onClick --> {_ => currentSlot.update(_ + 1)},                             // Clicking the button increments the current slot index
        onClick(_.delay(2000)) --> {_ => currentDisplay.update(_ + 1)}            // Clicking the button also increments the current display index, after a 2 second delay
      )
    )
  }

  def renderWheel(setCode: String): Element = {
    table(
      tbody{
        val wheel = rotateVector(Data.ModernSets, setCode, 5) // Put the desired set code at the 5th position
        val loopedWheel = wheel :+ wheel.head                 // Copy the first element to the end for a smooth loop
        loopedWheel.map(s =>                                                    // Translate the set codes into the appropriate set symbol
          tr( td( img(src := "set/" + fixCON(s) + "/M.svg", role := "img")))
          )
      }
    )
  }

end Main