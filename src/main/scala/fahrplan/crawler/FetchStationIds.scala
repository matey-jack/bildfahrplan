package fahrplan.crawler

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import de.schildbach.pte.BahnProvider
import de.schildbach.pte.dto.{Location, LocationType}
import org.json4s.jackson.Serialization

import scala.collection.JavaConverters._

case class HafasStation(id: String, name: String, place: String)

object HafasStation {
  def fromLocation(l: Location): HafasStation = {
    HafasStation(l.id, l.name, l.place)
  }
}

object FetchStationIds {
  val provider = new BahnProvider
  implicit val formats = org.json4s.DefaultFormats

  def main(args: Array[String]): Unit = {

    val scrapedStationLines = Files.readAllLines(Paths.get("raw_stations.json"), Charset.forName("UTF-8")).asScala
    val grouped = (scrapedStationLines take 20).par.flatMap { line =>
      val n: String = Serialization.read[ScrapedStation](line).name
      provider.suggestLocations(n).getLocations.asScala.
        filter(_.`type` == LocationType.STATION).toSet
    }.map(HafasStation.fromLocation)


    print(grouped)
  }
}

