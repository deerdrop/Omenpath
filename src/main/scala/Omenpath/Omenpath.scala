package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import _root_.Omenpath.Func.rotateVector
import _root_.Omenpath.Data.slotTimeUntilStop
import _root_.Omenpath.Data.slotKeyFrames

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
      div( cls := "banner",                     // Banner across the middle with translucent bg
        div( cls := "slots-main",             // Container for hiding and then rendering the slots
          cls <-- slotContainerClassSignal,   // Signal to have slots hidden at start
          children <-- setDataSignal.splitByIndex (renderSlot)
        ),
        button("Open the Paths", cls <-- btnClassSignal, onClick --> (_ => rollSets(Data.newSeason))),
      )
    )
  }

  def renderSlot(index: Int, initialSetCode: String, setCodeSignal: Signal[String]): Element = {
    div( cls := "slot-section",                              // Each section contains the slot itself, and a display.
      div ( cls := "display dotmatrix",
        p( initialSetCode,
          cls <-- tickStream.map { (tick: Int) =>            // Display begins empty, turns on when its slot has fully stopped.
            if (tick < slotKeyFrames(index) + slotTimeUntilStop) "hidden" else "" },
        )
      ),
      div( cls := "slot slot" + index,
        cls <-- tickStream.map{ (tick: Int) =>               // Slot begins in loop animation, then begins stop animation
          if (tick < slotKeyFrames(index)) "loop" else "stop" }, // when its keyframe tick is reached.
        div(cls := "slot-inner",
          renderWheel(initialSetCode)
        )
      )
    )
  }

  def renderWheel(setCode: String): Element = {
    table(
      tbody{
        val wheel = rotateVector(Data.ModernSets, setCode, 5) // Put the desired set code at the 5th position
        val loopedWheel = wheel :+ wheel.head                 // Copy the first element to the end for a smooth loop
        loopedWheel.map(s =>                                  // Translate the set codes into the appropriate set symbol
          tr( td( img(src := "set/" + fixCON(s) + ".svg", role := "img")))
        )
      }
    )
  }

end Main