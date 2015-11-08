package fahrplan.crawler

import scala.collection.JavaConverters._

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}

import de.schildbach.pte.BahnProvider

import scala.collection.{mutable, concurrent}
import org.json4s.jackson.Serialization

case class HafasStation(id : String, name : String, place : String)


object FetchStationIds {
  val provider = new BahnProvider
  implicit val formats = org.json4s.DefaultFormats

  def main(args: Array[String]): Unit = {

    val scrapedStationLines = Files.readAllLines(Paths.get("raw_stations.json"), Charset.forName("UTF-8")).asScala
    val s = scrapedStationLines.map(Serialization.read[ScrapedStation]).head
    println(provider.suggestLocations(s.name).getLocations)
    // val stationsById = concurrent.TrieMap[String, HafasStation]()
    // provider.suggestLocations().suggestedLocations

    // filter(_.type == LocationType.STATION)
  }
}

