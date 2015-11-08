package fahrplan.crawler

import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import de.schildbach.pte.BahnProvider
import de.schildbach.pte.dto.{Location, LocationType}
import org.json4s.jackson.Serialization

import scala.collection.JavaConverters._

case class HafasStation(id: String, name: String) extends Comparable[HafasStation] {
  override def compareTo(o: HafasStation): Int = if (o==null) 1 else id.compareTo(o.id)
}

object HafasStation {
  def fromLocation(l: Location): HafasStation = {
    HafasStation(l.id, l.name)
  }
}

object FetchStationIds {
  val provider = new BahnProvider
  implicit val formats = org.json4s.DefaultFormats

  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis()
    val scrapedStationLines = Files.readAllLines(Paths.get("raw_stations.json"), Charset.forName("UTF-8")).asScala
    val entries = scrapedStationLines.par.flatMap { line =>
      val scraped = Serialization.read[ScrapedStation](line)
      provider.suggestLocations(scraped.name).getLocations.asScala.
                      find(_.`type` == LocationType.STATION)
    }.map(s => HafasStation(s.id, s.name)).toSet.seq
    val queryTime = System.currentTimeMillis()
    println("querying done(ms)", queryTime - startTime)
    //print(entries)

    val p = new PrintWriter("stations_with_id.json")
    try {
      for (s <- entries) {
        p.println(Serialization.write(s))
      }
    } finally {
      p.close()
    }
    println("saving done(ms)", System.currentTimeMillis() - queryTime)

  }
}

