package fahrplan

import scala.collection.mutable.MutableList
import scala.collection.mutable

// TODO: Gleiswechsel, Fahrstraßen (Weichenabstand kürzer als Signalabstand)

class Abschnitt (v_max : Int = 160, länge: Int, bahnsteig : Boolean = false) {
  var weiter : Abschnitt = null
  var abzweige = new MutableList[Abschnitt]
  var von = new MutableList[Abschnitt]

  def ==>(a: Abschnitt) = weiter = a

  def <==(a: Abschnitt) = a ==> this

  def +> (a : Abschnitt): Unit =  {
    abzweige += a
  }

  def <+ (a : Abschnitt): Unit =  {
    a +> this
  }

  def ++> (as : List[Abschnitt]): Unit = {
    abzweige ++= as
  }

  def <++ (as : List[Abschnitt]): Unit = {
    as.foreach(_ +> this)
  }

}

case class GeradeStrecke(erster : Abschnitt, letzter : Abschnitt) {
  def ==>(a: Abschnitt) = letzter.weiter = a

  def <==(a: Abschnitt) = a ==> this.erster

  def +> (a : Abschnitt): Unit =  {
    letzter +> a
  }

  def <+ (a : Abschnitt): Unit =  {
    a +> this.erster
  }

  def ++> (as : List[Abschnitt]): Unit = {
    letzter ++> as
  }

  def <++ (as : List[Abschnitt]): Unit = {
    as.foreach(_ +> this.erster)
  }

  private def vorl(x : Abschnitt) : Abschnitt = {
    if (x.weiter == letzter) {
      x
    } else {
      vorl(x.weiter)
    }
  }
  def vorletzter() : Abschnitt = vorl(erster)

  /**
   * Geht davon aus, dass die andere Strecke in die Gegenrichtung verläuft und baut Abzweige hinter dem vorletzten
   * Element dieser Strecke und dem ersten Element der anderen Strecke (auf die jeweils andere Strecke).
   */
  def plus_Gleiswechsel(andere : GeradeStrecke) : Unit = {
    assert(erster != letzter)
    assert(andere.erster != andere.letzter)
    val v: Abschnitt = vorletzter()
    v +> andere.erster.weiter
    andere.erster +> v
  }
}

object GeradeStrecke {
  def auto_Abschnitte(v_max : Int, länge : Int, a_länge : Int) : GeradeStrecke = {
    val erster = new Abschnitt(v_max, länge.min(a_länge))
    if (länge < a_länge)
      GeradeStrecke(erster, erster)
    else {
      val (zweiter, letzter) = auto_Abschnitte(v_max, länge - a_länge, a_länge)
      erster.weiter = zweiter
      GeradeStrecke(erster, letzter)
    }
  }
}

trait Streckenpunkt {
  val strecken_km : Int
  def abstand(p : Streckenpunkt) = (strecken_km - p.strecken_km).abs
}

trait Bahnhof extends Streckenpunkt {
  val länge : Int = 400
  override def abstand(p : Streckenpunkt): Int = {
    if (strecken_km > p.strecken_km) 
      p.abstand(this)
    else
      p.strecken_km - strecken_km - länge
  }
  def steig(v : Int = 80) = new Abschnitt(v, länge, true)
  def durchgang(v : Int = 160) = new Abschnitt(v, länge, false)
}

object Strecken {
  def fülle_von(samen : List[Abschnitt]): Unit = {
    val erledigt = new mutable.HashSet[Abschnitt]()
    val todo = new mutable.Queue[Abschnitt]()
    todo ++= samen
    while (todo.nonEmpty) {
      val a = todo.dequeue()
      if (!erledigt.contains(a)) {
        a.weiter.von += a
        a.abzweige.foreach(_.von += a)
        erledigt += a
      }
    }
  }

  
}