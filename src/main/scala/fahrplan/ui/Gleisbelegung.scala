package fahrplan.ui

import java.util.Date
import java.util.concurrent.TimeUnit

import fahrplan.model.Abfahrten

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

object Gleisbelegung extends JFXApp {
  val fenster_breite: Double = 800
  val fenster_höhe: Double = 1100
  val main_pane = new Pane()

  stage = new JFXApp.PrimaryStage {
    title.value = "Gleisbelegung"
    width = fenster_breite
    height = fenster_höhe
    scene = new Scene {
      root = main_pane
    }
  }

  def diff_in_Minutes(a: Date, b: Date): Long = {
    TimeUnit.MILLISECONDS.toMinutes(a.getTime - b.getTime)
  }

  def zeichne_abfahrten(): Unit = {
    val abfahrten = Abfahrten.request_for_station("8010404")
    val erste_abfahrt = abfahrten.head.abfahrt.get
    val zahl_gleise = 4
    val min_gleis = 3
    val bahnsteig_höhe = 20
    val bahnsteig_breite = stage.getWidth / 2 / (zahl_gleise + 1)
    main_pane.children = abfahrten.flatMap((abfahrt) => Seq(new Rectangle {
      x = bahnsteig_breite * (2 * (abfahrt.gleis.toInt - min_gleis) + 1)
      y = bahnsteig_höhe / 2 * diff_in_Minutes(abfahrt.abfahrt.get, erste_abfahrt)
      width = bahnsteig_breite
      height = bahnsteig_höhe
      fill = abfahrt.color
    }, new Text(x = bahnsteig_breite * 2 * (abfahrt.gleis.toInt - min_gleis + 1),
      y = bahnsteig_höhe / 2 * (diff_in_Minutes(abfahrt.abfahrt.get, erste_abfahrt) + 1),
      abfahrt.zug_name)
    ))
  }

  zeichne_abfahrten()
}