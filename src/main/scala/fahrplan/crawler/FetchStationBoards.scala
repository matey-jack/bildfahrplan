package fahrplan.crawler

import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util.{Calendar, GregorianCalendar}

import de.schildbach.pte.BahnProvider
import org.json4s.jackson.Serialization

import scala.collection.JavaConverters._

case class StationStats(id : String, name : String,
                        nIntercity : Int, nRegional : Int, nSuburban : Int)


object FetchStationBoards {
  val provider = new BahnProvider
  implicit val formats = org.json4s.DefaultFormats

  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis()
    val the_date = new GregorianCalendar(2015, Calendar.NOVEMBER, 20, 15, 0)
    val stationLines = Files.readAllLines(Paths.get("stations_with_id-golden.json"), Charset.forName("UTF-8")).asScala
    val entries = (stationLines take 10).par.map { line =>
      val station = Serialization.read[HafasStation](line)
      provider.queryDepartures(station.id, the_date.getTime, 5000, false).
        stationDepartures.asScala.flatMap(_.departures.asScala)
    }.seq
    val queryTime = System.currentTimeMillis()
    println("querying done(ms)", queryTime - startTime)
    //print(entries)

    val p = new PrintWriter("station_departures.json")
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
