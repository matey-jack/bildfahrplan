package fahrplan.ui

import java.util.Date
import java.util.concurrent.TimeUnit

import fahrplan.model.Abfahrten

import scala.collection.JavaConverters._

import scalafx.application.JFXApp
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.ScrollPane
import scalafx.scene.layout._
import scalafx.scene.shape.{Shape, Rectangle}
import scalafx.scene.text.Text

object Gleisbelegung extends JFXApp {
  val fenster_breite: Double = 800
  val fenster_höhe: Double = 800
  val main_pane = new Pane()

  stage = new JFXApp.PrimaryStage {
    title.value = "Gleisbelegung"
    width = fenster_breite
    height = fenster_höhe
    scene = new Scene {
      root = new ScrollPane() {
        content = main_pane
      }
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
    // Text has no getY, so we just count Rectangles
    val main_height = main_pane.children.asScala.collect { case (node : javafx.scene.shape.Rectangle) => node.getY }.max
    main_pane.setPrefHeight(main_height + 2 * bahnsteig_höhe)
  }

  zeichne_abfahrten()
}