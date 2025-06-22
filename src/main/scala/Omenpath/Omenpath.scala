package Omenpath

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import _root_.Omenpath.Func.rotateVector
import _root_.Omenpath.Data.slotTimeUntilStop
import _root_.Omenpath.Data.slotKeyFrames
import _root_.Omenpath.Func.getScryfallQuery

@main
def Omenpath(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  val model = new Model
  import model.*

  def appElement(): Element =
    div(
      h1(idAttr := "title", "Omenpath Season " + Data.newSeason + " Rotation"),
      div(cls := "banner",                  // Banner across the middle with translucent bg
        div(
          cls := "slots-main",              // Container for hiding and then rendering the slots
          cls <-- slotContainerClassSignal, // Signal to have slots hidden at start
          children <-- setDataSignal.splitByIndex(renderSlot)
        ),
        button(
          "Open the Paths",
          cls <-- btnClassSignal,
          onClick --> (_ => rollSets(Data.newSeason))
        )
      ),
      div(
        visibility <-- tickStream.withCurrentValueOf(slotContainerClassSignal).map { (tick: Int, signal: String) => if (tick < slotKeyFrames(6)) "hidden" else signal},
        scryfallButton()
      )
    )

  def renderSlot(
      index: Int,
      initialSetCode: String,
      setCodeSignal: Signal[String]
  ): Element =
    div(cls := "slot-section",                // Each section contains the slot itself, and a display.
      div(
        cls := "display dotmatrix",
        p(
          initialSetCode,
          cls <-- tickStream.map {
            (tick: Int) =>                    // Display begins empty, turns on when its slot has fully stopped.
              if (tick < slotKeyFrames(index) + slotTimeUntilStop) "hidden"
              else ""
          }
        )
      ),
      div(
        cls := "slot slot" + index,
        cls <-- tickStream.map {
          (tick: Int) => // Slot begins in loop animation, then begins stop animation
            if (tick < slotKeyFrames(index)) "loop" else "stop"
        }, // when its keyframe tick is reached.
        div(cls := "slot-inner", renderWheel(initialSetCode))
      )
    )

  def renderWheel(setCode: String): Element =
    table(
      tbody {
        val wheel = rotateVector(
          Data.ModernSets,
          setCode,
          5
        ) // Put the desired set code at the 5th position
        val loopedWheel =
          wheel :+ wheel.head // Copy the first element to the end for a smooth loop
        loopedWheel.map(
          s => // Translate the set codes into the appropriate set symbol
            tr(td(img(src := "set/" + fixCON(s) + ".svg", role := "img")))
        )
      }
    )

  def scryfallButton(): Element = {
    svg.svg(
      svg.width   := "190px",
      svg.height  := "120px",
      svg.viewBox := "0 0 375 238",
      svg.xmlns   := "http://www.w3.org/2000/svg",
      svg.xmlnsXlink := "http://www.w3.org/1999/xlink",
      svg.defs(
        svg.linearGradient(
          svg.idAttr            := "linearGradient3260",
          svg.x1                := "250",
          svg.x2                := "250",
          svg.y1                := "53.1",
          svg.y2                := "645",
          svg.gradientTransform := "matrix(.267 0 0 .34 32.9 43.1)",
          svg.gradientUnits     := "userSpaceOnUse",
          svg.stop(svg.stopColor := "#1d1c25", svg.offsetAttr := "0"),
          svg.stop(svg.stopColor := "#431e3f", svg.offsetAttr := "1")
        )
      ),
      svg.a(
        svg.href <-- setDataSignal.map {
          "https://scryfall.com/search?" + getScryfallQuery(_)
        },
        svg.target := "_blank",
        svg.g(
          svg.transform := "translate(37.7 -15.8)",
          svg.path(
            svg.d := "m-34.4 29.1v172c7.66 21.6 20.9 27.3 34.4 32.1h206c0.84 10 1.89 16.6 3.16 17.6 7.49 6.2 125-90.9 125-103s-117-110-125-103c-1.26 1.04-2.3 7.48-3.14 17.4h-206c-14.3-2.01-25.2-14.5-34.4-32.1z",
            svg.fill             := "url(#linearGradient3260)",
            svg.stopColor        := "#000000",
            svg.stroke           := "#000",
            svg.strokeMiterLimit := "13",
            svg.strokeOpacity    := ".515",
            svg.strokeWidth      := "6.55",
            svg.style            := "font-variation-settings:normal;paint-order:stroke fill markers"
          ),
          svg.g(
            svg.transform    := "matrix(.315 0 0 .315 17.8 56.1)",
            svg.fillRule     := "evenodd",
            svg.circle(svg.cx := "290", svg.cy := "288", svg.r := "230", svg.opacity := ".09"),
            svg.path(
              svg.d := "m280 113-0.981 362 44.1 7.07 56.7-13.2 28.5-72.5-28-179-31.9-113",
              svg.fill := "#bc979d"
            ),
            svg.path(
              svg.d := "m282 101-2.46 383-68-21.9 13.9-356",
              svg.fill := "#ae7f9c"
            ),
            svg.path(
              svg.d := "m207 113v344s-120-62.8-114-174c6.02-111 114-170 114-170z",
              svg.fill := "#786076"
            ),
            svg.path(
              svg.d := "m237 107-30.6 4.35s-20.7 10.4-37.9 25.5c-75.2 168 108 115-12.7 287 50.6 47.9 72.3 41.1 72.3 41.1l8.96-358z",
              svg.fill := "#947a92"
            ),
            svg.path(
              svg.d := "m343 90c-109-29.3-222 35.6-251 145s35.6 222 145 251 222-35.6 251-145-35.6-222-145-251zm-86.7 362 0.276 0.71c1.17 3.19 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.387l48.4-17.8c3.18-1.18 6.71-1.04 9.79 0.388 3.08 1.42 5.47 4.01 6.64 7.2l0.19 0.56c2.1 5.85-0.304 12.4-5.71 15.4-93.2 22.2-188-30.7-218-122s14.7-190 103-227c5.14 0.502 9.54 3.91 11.3 8.76 2.58 6.98 10.3 10.6 17.3 7.99l15.7-5.8c3.19-1.18 6.71-1.04 9.79 0.387 3.08 1.42 5.47 4.01 6.64 7.2l0.19 0.56c1.17 3.18 1.03 6.71-0.39 9.79-1.42 3.08-4.01 5.47-7.2 6.64l-109 40.4c-3.19 1.17-5.78 3.56-7.2 6.64-1.42 3.08-1.56 6.6-0.388 9.79l0.19 0.56c1.17 3.19 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.388l80.1-29.5c3.18-1.17 6.7-1.03 9.79 0.388 3.08 1.42 5.47 4.01 6.64 7.2l0.19 0.56c1.17 3.18 1.03 6.7-0.39 9.79-1.42 3.08-4.01 5.47-7.2 6.64l-128 47.1c-3.19 1.17-5.78 3.56-7.2 6.64-1.42 3.08-1.56 6.6-0.387 9.79l0.19 0.56c1.17 3.19 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.388l157-57.7c3.18-1.18 6.7-1.04 9.79 0.387s5.47 4.01 6.64 7.2l0.467 1.27c1.17 3.19 1.04 6.71-0.388 9.79-1.42 3.08-4.01 5.47-7.2 6.64l-113 41.5c-3.19 1.17-5.78 3.56-7.2 6.64-1.42 3.08-1.56 6.6-0.387 9.79l0.19 0.56c1.17 3.18 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.388l51.8-19.1c3.19-1.17 6.71-1.03 9.79 0.39 3.08 1.42 5.47 4.01 6.64 7.2l0.19 0.56c1.17 3.18 1.03 6.71-0.39 9.79-1.42 3.08-4.01 5.47-7.2 6.64l-89.1 32.8c-3.18 1.17-5.77 3.56-7.2 6.64s-1.56 6.6-0.388 9.79l0.19 0.56c1.17 3.18 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.388l121-44.4c3.18-1.17 6.71-1.03 9.79 0.39 3.08 1.42 5.47 4.01 6.64 7.2l0.19 0.56c1.17 3.18 1.04 6.71-0.388 9.79s-4.01 5.47-7.2 6.64l-75 27.5c-3.19 1.17-5.78 3.56-7.2 6.64-1.42 3.08-1.56 6.6-0.387 9.79l0.19 0.56c1.17 3.19 3.56 5.78 6.64 7.2 3.08 1.42 6.6 1.56 9.79 0.387l94.1-34.5c3.18-1.18 6.71-1.04 9.79 0.388s5.47 4.01 6.64 7.2c2.43 6.58-0.893 13.9-7.45 16.4l-86.9 33.2c-3.18 1.18-5.76 3.57-7.18 6.66-1.41 3.08-1.55 6.6-0.367 9.78l-0.018-0.09z",
              svg.fill := "#fff"
            )
          )
        )
      )
    )
  }
end Main
