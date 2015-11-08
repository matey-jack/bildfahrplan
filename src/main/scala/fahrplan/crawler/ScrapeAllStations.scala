package fahrplan.crawler

import java.io.PrintWriter

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.json4s._
import org.json4s.jackson.Serialization
import com.fasterxml.jackson.databind.SerializationFeature


abstract class ScrapeEntry

case class ScrapedStation(kuerzel: String, name: String, typ: String) extends ScrapeEntry

case class WeirdStuff(stuff: String) extends ScrapeEntry


object ScrapeAllStations {

  private val baseUrl = "http://www.bahnseite.de/DS100/"

  def main(args: Array[String]): Unit = {
    val browser = new Browser
    val mainDoc = browser.get(baseUrl ++ "DS100ofr.html")

    val entries: Seq[ScrapeEntry] = for (
      url <- (mainDoc >> elementList("a")) map (_.attr("href"))
      if url.startsWith("DS100");
      row <- browser.get(baseUrl + url) >> elementList("tr")
    )
      yield row >> texts("td") filter (!_.isEmpty()) match {
        case Seq(kuerzel, name, typ) => new ScrapedStation(kuerzel, name, typ)
        case stuff => WeirdStuff(url ++ "  --->  " ++ stuff.toString)
      }

    for (WeirdStuff(stuff) <- entries) {
      //      println(stuff)
    }

    implicit val formats = Serialization.formats(NoTypeHints)
    // following method unavailable in the release we're pulling.
    // therefore we can't serialize directy into the Writer. need to use intermediate String.
    // org.json4s.jackson.JsonMethods.configure(SerializationFeature.CLOSE_CLOSEABLE, false)

    val p = new PrintWriter("raw_stations.json")
    try {
      for (s@ScrapedStation(_, _, _) <- entries) {
        p.println(Serialization.write(s))
      }
    } finally {
      p.close()
    }
    // val jsonStations = entries.filter{case _ : StationEntry => true}.map(Serialization.write)
    // Files.write(Paths.get("raw_stations.json"), "file contents".getBytes(StandardCharsets.UTF_8))

  }
}
