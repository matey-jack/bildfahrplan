package fahrplan

import scala.collection.mutable.MutableList

case class Abzweig( a : Abschnitt, v : Int ) {

}

// TODO: Gleiswechsel, Fahrstraßen (Weichenabstand kürzer als Signalabstand)

class Abschnitt (v_max : Int = 160, länge: Int, bahnsteig : Boolean = false) {
  var weiter : Abschnitt = null
  var abzweige : MutableList[Abschnitt] = new MutableList[Abschnitt]
  var von : List[Abschnitt] = null

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

object BerlinHauptbahnhof extends Bahnhof {
  val strecken_km = 0
  override val länge = 600
}

object BerlinJungfernheide extends Bahnhof {
  val strecken_km = 5650
}

// In allen Bhfn sind die Bahnsteige von Süden nummeriert.
// Streckengleise teilweise auch.
object BahnhofSpandau extends Bahnhof {
  val strecken_km = 12428
  override val länge = 600
  var gütergleis = durchgang()
  var steig_1 = steig()
  var steig_2 = steig()
  var steig_3 = steig()
  var steig_4 = steig()
}

object BahnhofFalkensee extends Bahnhof {
  val strecken_km = 20373
  var steig_1 = steig()
  var durch_1 = durchgang()
  var durch_2 = durchgang()
  var steig_2 = steig()
}

object BahnhofBrieselang extends Bahnhof {
  val strecken_km = 26950
  var steig_1 = steig()
  var durch_1 = durchgang()
  var durch_2 = durchgang()
  var steig_2 = steig()
}

object BahnhofNauen extends Bahnhof {
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
    val spandau_einwärts = mk_strecke(160, BahnhofSpandau.abstand(BahnhofFalkensee), 1000)
    spandau_auswärts.plus_Gleiswechsel(spandau_einwärts) // auf Falkenseer Seite
    spandau_einwärts.plus_Gleiswechsel(spandau_auswärts) // auf Spandauer Seite

    spandau_einwärts ++> List(BahnhofSpandau.steig_1, BahnhofSpandau.steig_2)
    spandau_einwärts <++ List(BahnhofFalkensee.steig_1, BahnhofFalkensee.durch_1)

    spandau_auswärts <++ List(BahnhofSpandau.steig_3, BahnhofSpandau.steig_4)
    spandau_auswärts ++> List(BahnhofFalkensee.durch_2, BahnhofFalkensee.steig_2)

    val fb_1 = mk_strecke(160, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_2 = mk_strecke(200, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_3 = mk_strecke(200, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_4 = mk_strecke(160, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)

    fb_1 <== BahnhofFalkensee.steig_1; fb_1 ==> BahnhofBrieselang.steig_1
    fb_2 <== BahnhofFalkensee.durch_1; fb_2 ==> BahnhofBrieselang.durch_1
    fb_3 <== BahnhofBrieselang.durch_2; fb_3 ==> BahnhofFalkensee.durch_2
    fb_4 <== BahnhofBrieselang.steig_2; fb_4 ==> BahnhofFalkensee.steig_2

    val bn_1 = mk_strecke(200, BahnhofBrieselang.abstand(BahnhofNauen), 1500)
    val bn_2 = mk_strecke(200, BahnhofBrieselang.abstand(BahnhofNauen), 1500)
    bn_1.plus_Gleiswechsel(bn_2) // vor Nauen

    bn_1 <== BahnhofBrieselang.durch_1; bn_1 ==> BahnhofNauen.durchgang_nach_Hamburg
    bn_1 <+ BahnhofBrieselang.steig_1; bn_1 ++> List(BahnhofNauen.steig_1, BahnhofNauen.steig_2)

    bn_2 <== BahnhofNauen.durchgang_von_Hamburg; bn_2 ==> BahnhofBrieselang.durch_2
    bn_2 <++ List(BahnhofNauen.steig_1, BahnhofNauen.steig_2, BahnhofNauen.steig_4, BahnhofNauen.steig_5, BahnhofNauen.durchgang_6)
    bn_2 +> BahnhofBrieselang.steig_2
  }

  def mk_strecke(v_max : Int, länge : Int, a_länge : Int) : GeradeStrecke = {
    val erster = new Abschnitt(v_max, länge.min(a_länge))
    if (länge < a_länge)
      GeradeStrecke(erster, erster)
    else {
      val (zweiter, letzter) = mk_strecke(v_max, länge - a_länge, a_länge)
      erster.weiter = zweiter
      GeradeStrecke(erster, letzter)
    }
  }
}
