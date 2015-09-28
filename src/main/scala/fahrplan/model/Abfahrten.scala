package fahrplan.model

import java.text.DateFormat

import de.schildbach.pte.dto.Departure
import de.schildbach.pte.dto.Product._

import scala.collection.JavaConverters._

import java.util.Date

import de.schildbach.pte.BahnProvider
import de.schildbach.pte.dto.QueryDeparturesResult.Status

case class Abfahrt(ankunft : Option[Date], abfahrt : Option[Date],
                   zug_name : String, gleis : String,
                   von : Option[String], nach : Option[String]) {
  def nur_bahn() : Boolean = {
    zug_name.charAt(0) != 'S'
  }
}

object Abfahrt {
  // val just_time = DateFormat.
  def from_departure(d : Departure) : Abfahrt = {
    Abfahrt(
      ankunft = Option.empty,
      abfahrt = Option.apply(d.plannedTime),
      zug_name = d.line.label,
      gleis = if (d.position != null) d.position.toString else "",
      von = Option.empty,
      nach = Option.apply(d.destination.name)
    )
  }
}

object Abfahrten {
  def request_for_station(station_id : String): Seq[Abfahrt] = {
    val provider = new BahnProvider
    val queryResult = provider.queryDepartures(station_id, new Date, 200, true)
    assert(queryResult.status == Status.OK)
    queryResult.stationDepartures.asScala
      .flatMap((x) => x.departures.asScala)
      .filter((d) => Set(HIGH_SPEED_TRAIN, REGIONAL_TRAIN).contains(d.line.product))
      .map(Abfahrt.from_departure)
  }

  def main(args: Array[String]): Unit = {
    val result = request_for_station("8010404")
    print(result.head)
  }
}