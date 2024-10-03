package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

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
      div(cls <-- getSlotClasses(index), 
        renderWheel()
      ),
      button(cls := "round",
        disabled <-- currentSlotSignal.map(_ != index),
        onClick --> {_ => progressSlots()}
      )
    )
  }

  def renderWheel(): Element = {
    div(cls := "slot-inner",
      table(
        children <-- Var(Data.ModernSets).signal.map(setList => setList.map { setCode => tr( td( img(src := "set/" + setCode + "/M.svg", role := "img")))}),
        tr(
          td(
            img(src := "set/" + Data.ModernSets(0) + "/M.svg", role := "img")
          )
        )
      )
    )
  }

end Main