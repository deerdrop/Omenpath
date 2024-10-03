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
      div( cls <-- slotContainerClassSignal,
        div( cls := "slots",
          children <-- setDataSignal.splitByIndex (renderSlot)
        )
      ),
      button("Open the Paths", cls <-- btnClassSignal, onClick --> (_ => rollSets(Data.newSeason))),
    )
  }

  def renderSlot(index: Int, initialSetCode: String, setCodeSignal: Signal[String]): Element = {
    div(
      div( cls := "slot",
        cls <-- currentSlotSignal.map{ (current: Int) =>
          if (index < current) "stop" else "loop loop" + index}, 
        div(cls := "slot-inner",
          renderWheel(initialSetCode)
        )
      ),
      button(cls := "round",
        disabled <-- currentSlotSignal.map(_ != index),
        onClick --> {_ => currentSlot.update(_ + 1)}
      )
    )
  }

  def renderWheel(setCode: String): Element = {
    table(
      tbody{
        val wheel = rotateVector(Data.ModernSets, setCode, 5)
        val loopedWheel = wheel :+ wheel.head
        loopedWheel.map(s =>
          tr( td( img(src := "set/" + s + "/M.svg", role := "img")))
          )
      }
    )
  }

end Main