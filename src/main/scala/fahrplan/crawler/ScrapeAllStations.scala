package fahrplan.crawler

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.collection.GenTraversableLike

case class StationEntry(kuerzel : String, name : String, remark : String, typ : String)
case class WeirdStuff(stuff : String)


object ScrapeAllStations {

  private val baseUrl = "http://www.bahnseite.de/DS100/"

  def lastN[A, Repr](list : GenTraversableLike[A, Repr], n : Int) : Option[Repr] = {
    if (list.size < 3)
      None
    else
      Some(list.slice(list.size - 3, list.size))
  }
  
  def main(args: Array[String]): Unit = {
    val browser = new Browser
    val mainDoc = browser.get(baseUrl ++ "DS100ofr.html")

    val entries = for (
      url <- (mainDoc >> elementList("a")) map (_.attr("href"))
      if url.startsWith("DS100");
      row <- browser.get(baseUrl + url) >> elementList("tr")
    ) 
    yield row >> texts("td") filter (!_.isEmpty()) match {
      case Seq(kuerzel, name, typ) => new StationEntry(kuerzel, name, "", typ)
      case stuff => WeirdStuff(url ++ "  --->  " ++ stuff.toString)
    }

    for ( WeirdStuff(stuff) <- entries) {
      println(stuff)
    }
  }
}
