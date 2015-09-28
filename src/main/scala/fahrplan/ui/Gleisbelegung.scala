package fahrplan.ui

import java.util.Date
import java.util.concurrent.TimeUnit

import fahrplan.model.Abfahrten

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.{Node, Scene}
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

object Gleisbelegung extends JFXApp {
  val gleise = Seq(1, 2, 3, 4).map((x) => "Gleis " ++ x.toString)
  val fenster_breite: Double = 600
  val fenster_höhe: Double = 450
  val main_pane = new Pane()

  stage = new JFXApp.PrimaryStage {
    title.value = "Gleisbelegung"
    width = fenster_breite
    height = fenster_höhe
    scene = new Scene {
      root = new VBox(
        new HBox(gleise.map((x) => new Text(x) {
          maxWidth(Double.MaxValue)
          hgrow = Priority.Always
        }) : _*) {
          hgrow = Priority.Always
        },
        main_pane
      ) {
        hgrow = Priority.Always
      }
    }
  }

  def diff_in_Minutes(a : Date, b : Date) : Long = {
    TimeUnit.MILLISECONDS.toMinutes(a.getTime - b.getTime)
  }

  def zeichne_abfahrten(): Unit = {
    val abfahrten = Abfahrten.request_for_station("8010404")
    val erste_abfahrt = abfahrten.head.abfahrt.get
    val bahnsteig_höhe = 20
    val bahnsteig_breite = stage.getWidth / (2 * gleise.length + 1)
    main_pane.children= abfahrten.map((abfahrt) => new Rectangle {
      x = bahnsteig_breite * (1 + abfahrt.gleis.toInt)
      y = bahnsteig_höhe / 2 * diff_in_Minutes(abfahrt.abfahrt.get, erste_abfahrt)
      width = bahnsteig_breite
      height = bahnsteig_höhe
    })
  }
  zeichne_abfahrten()
}