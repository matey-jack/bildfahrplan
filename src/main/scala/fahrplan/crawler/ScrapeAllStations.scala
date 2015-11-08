package fahrplan.crawler

import java.io.PrintWriter

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import org.json4s._
import org.json4s.jackson.Serialization
// import org.json4s.jackson.Serialization.{read, write}

import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

abstract class ScrapeEntry
case class StationEntry(kuerzel : String, name : String, typ : String) extends ScrapeEntry
case class WeirdStuff(stuff : String) extends ScrapeEntry

object ScrapeAllStations {

  private val baseUrl = "http://www.bahnseite.de/DS100/"

  def main(args: Array[String]): Unit = {
    val browser = new Browser
    val mainDoc = browser.get(baseUrl ++ "DS100ofr.html")

    val entries : Seq[ScrapeEntry] = for (
      url <- (mainDoc >> elementList("a")) map (_.attr("href"))
      if url.startsWith("DS100");
      row <- browser.get(baseUrl + url) >> elementList("tr")
    ) 
    yield row >> texts("td") filter (!_.isEmpty()) match {
      case Seq(kuerzel, name, typ) => new StationEntry(kuerzel, name, typ)
      case stuff => WeirdStuff(url ++ "  --->  " ++ stuff.toString)
    }

    implicit val formats = Serialization.formats(NoTypeHints)

//    for ( WeirdStuff(stuff) <- entries) {
//      println(stuff)
//    }

    val p = new java.io.PrintWriter("raw_stations.json")
    try {
      for ( s @ StationEntry(_,_,_) <- entries) {
        p.println(Serialization.write(s))
      }
    } finally {
      p.close()
    }
    // val jsonStations = entries.filter{case _ : StationEntry => true}.map(Serialization.write)
    // Files.write(Paths.get("raw_stations.json"), "file contents".getBytes(StandardCharsets.UTF_8))

  }
}
