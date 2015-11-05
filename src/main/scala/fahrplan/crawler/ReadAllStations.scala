package fahrplan.crawler
/*
import scala.xml._
import dispatch._, Defaults._

object ReadAllStations {

  private val baseUrl = "http://www.bahnseite.de/DS100/"

  def main(args: Array[String]): Unit = {
    val linkUrl = url(baseUrl ++ "DS100ofr.html")
    val linkFuture = Http(linkUrl OK as.String)

    for (linkHtml <- linkFuture) {
      val linkXml = XML.loadString(linkHtml)
      val links = linkXml \\ "a" \ "@href" filter (_.asInstanceOf[String].startsWith("DS100"))
      // XML is too strict to parse HTML
      // we could try this: http://www.scala-lang.org/api/2.10.4/index.html#scala.xml.parsing.XhtmlParser
      // or: http://michel-daviot.blogspot.de/2013/01/processing-html-with-scala-as-if-xml.html
      
      println(linkHtml)
    }
  }


}
*/