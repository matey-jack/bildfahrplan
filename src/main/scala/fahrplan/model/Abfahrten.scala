package fahrplan.model

import de.schildbach.pte.dto.Departure
import de.schildbach.pte.dto.Product._

import scala.collection.JavaConverters._

import java.util.{GregorianCalendar, Calendar, Date}

import de.schildbach.pte.BahnProvider
import de.schildbach.pte.dto.QueryDeparturesResult.Status

import scalafx.scene.paint.Color

case class Abfahrt(ankunft : Option[Date], abfahrt : Option[Date],
                   zug_name : String, gleis : String, zug_typ : de.schildbach.pte.dto.Product,
                   von : Option[String], nach : Option[String]) {
  def nur_bahn() : Boolean = {
    zug_name.charAt(0) != 'S'
  }

  def color = if (zug_typ == REGIONAL_TRAIN) Color.Crimson else Color.DarkBlue
}

object Abfahrt {
  // val just_time = DateFormat.
  def from_departure(d : Departure) : Abfahrt = {
    Abfahrt(
      ankunft = Option.empty,
      abfahrt = Option.apply(d.plannedTime),
      zug_name = d.line.label.takeWhile(_.isLetter) ++ " " ++ d.destination.name,
      zug_typ = d.line.product,
      gleis = if (d.position != null) d.position.name else "",
      von = Option.empty,
      nach = Option.apply(d.destination.name)
    )
  }
}

object Abfahrten {
  def request_for_station(station_id : String): Seq[Abfahrt] = {
    val provider = new BahnProvider
    val the_date = new GregorianCalendar(2015, Calendar.NOVEMBER, 28, 19, 0)
    val queryResult = provider.queryDepartures(station_id, the_date.getTime, 200, true)
    assert(queryResult.status == Status.OK)

    val last_departure: Date = queryResult.stationDepartures.asScala.last.departures.asScala.last.plannedTime
    val otherResult = provider.queryDepartures(station_id, last_departure, 200, true)
    assert(otherResult.status == Status.OK)

    (queryResult.stationDepartures.asScala ++ otherResult.stationDepartures.asScala)
      .flatMap((x) => x.departures.asScala)
      .filter((d) => Set(HIGH_SPEED_TRAIN, REGIONAL_TRAIN).contains(d.line.product))
      .map(Abfahrt.from_departure)
  }

  def main(args: Array[String]): Unit = {
    val result = request_for_station("8010404")
    print(result.head)
  }
}