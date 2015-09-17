package fahrplan

import scala.collection.mutable.MutableList

case class Abzweig( a : Abschnitt, v : Int ) {

}

// TODO: Gleiswechsel, Fahrstraßen (Weichenabstand kürzer als Signalabstand)

class Abschnitt (v_max : Int = 160, länge: Int, bahnsteig : Boolean = false) {
  var weiter : Abschnitt = null
  var abzweige : MutableList[Abschnitt] = new MutableList[Abschnitt]
  var von : List[Abschnitt] = null

  def plus_Abzweig(a : Abschnitt) {
    abzweige += a
  }
}

case class GeradeStrecke(erster : Abschnitt, letzter : Abschnitt) {
  def vorletzter() : Abschnitt = {
    def vorl(x : Abschnitt) : Abschnitt = {
      if (x.weiter == letzter || x.abzweige.contains(letzter)) {
        return x
      } else {
        return vorl(x.weiter)
      }
    }
    return vorl(erster)
  }
  def plus_Gleiswechsel(andere : GeradeStrecke) : Unit = {
    assert(erster != letzter)
    assert(andere.erster != andere.letzter)
    val v: Abschnitt = vorletzter()
    v.plus_Abzweig(andere.erster.weiter)
    andere.erster.plus_Abzweig(v)
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
      return p.abstand(this)
    return p.strecken_km - strecken_km - länge
  }
  def steig(v : Int = 80) = new Abschnitt(v, länge, true)
  def durchgang(v : Int = 160) = new Abschnitt(v, länge, false)
}

object BerlinHauptbahnhof extends Streckenpunkt with Bahnhof {
  val strecken_km = 0
  override val länge = 600
}

object BerlinJungfernheide extends Streckenpunkt with Bahnhof {
  val strecken_km = 5650
}

object BahnhofSpandau extends Streckenpunkt with Bahnhof {
  val strecken_km = 12428
  override val länge = 600
  var gütergleis = durchgang()
  var steig_1 = steig()
  var steig_2 = steig()
  var steig_3 = steig()
  var steig_4 = steig()
}

object BahnhofFalkensee extends Streckenpunkt with Bahnhof {
  val strecken_km = 20373
  var steig_1 = steig()
  var durch_1 = durchgang()
  var durch_2 = durchgang()
  var steig_2 = steig()
}

object BahnhofBrieselang extends Streckenpunkt with Bahnhof {
  val strecken_km = 26950
  var steig_1 = steig()
  var durch_1 = durchgang()
  var durch_2 = durchgang()
  var steig_2 = steig()
}

object BahnhofNauen extends Streckenpunkt with Bahnhof {
  val strecken_km = 35370
  var durchgang_von_Hamburg = durchgang(v=200)
  var steig_1 = steig()
  var steig_2 = steig()
  var durchgang_nach_Hamburg = durchgang(v=200)
  var steig_4 = steig()
  var steig_5 = steig()
  var durchgang_6 = durchgang(v = 80)
}

object HamburgerBahn {
  def mk_bahn(): Unit = {
    val spandau_auswärts = mk_strecke(160, BahnhofSpandau.abstand(BahnhofFalkensee), 1000)
    List(BahnhofSpandau.steig_2, BahnhofSpandau.steig_3, BahnhofSpandau.steig_3).map(_.plus_Abzweig(spandau_auswärts.erster))
    spandau_auswärts.letzter.abzweige ++= List(BahnhofFalkensee.durch_2, BahnhofFalkensee.steig_2)

    val spandau_einwärts = mk_strecke(160, BahnhofSpandau.abstand(BahnhofFalkensee), 1000)
    List(BahnhofFalkensee)
  }
  def mk_strecke(v_max : Int, länge : Int, a_länge : Int) : GeradeStrecke = {
    val erster = new Abschnitt(v_max, länge.min(a_länge))
    if (länge < a_länge)
      return GeradeStrecke(erster, erster)
    val (zweiter, letzter) = mk_strecke(v_max, länge - a_länge, a_länge)
    erster.weiter = zweiter
    return GeradeStrecke(erster, letzter)
  }
}
