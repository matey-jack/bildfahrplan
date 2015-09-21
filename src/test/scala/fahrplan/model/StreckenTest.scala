package fahrplan.model

import org.scalatest._

class StreckenTest extends FlatSpec with Matchers {
  HamburgerBahn   // force execution of constructors ;-)

  "Verbindung" should "recognize base case" in {
    val a = abschnitt()
    a.ist_verbunden(a) should be(true)
  }

  def abschnitt(): Abschnitt = {
    new Abschnitt(länge = 100)
  }

  it should "recognize simple case" in {
    val a = abschnitt()
    val b = abschnitt()
    a ==> b
    a.weiter should be (b)
    a.ist_verbunden(b) should be(true)
  }

  "Strecke" should "verbunden" in {
    val s = GeradeStrecke.auto_Abschnitte(länge = 2000, a_länge = 600)

    s.erster.ist_verbunden(s.letzter) should be (true)
  }

  "Abzweig" should "verbinden" in {
    val a = abschnitt()
    val b = abschnitt()
    a +> b

    a.ist_verbunden(b) should be (true)
  }

//  "Gleiswechsel" should "alles verbinden" in {
//    val s1 = GeradeStrecke.auto_Abschnitte(länge = 2000, a_länge = 600)
//    val s2 = GeradeStrecke.auto_Abschnitte(länge = 2000, a_länge = 600)
//    s1.plus_Gleiswechsel(s2)
//
//    s1.erster.ist_verbunden(s2.letzter) should be (true)
//    s2.erster.ist_verbunden(s1.letzter) should be (true) // das ist noch nicht durchdacht!!
//  }

  "Hamburger Bahn" should "go from Spandau to Falkensee" in {
    BahnhofSpandau.steig_3.ist_verbunden(BahnhofFalkensee.steig_2) should be(true)
  }

  it should "go from Falkensee to BahnhofBrieselang" in {
    BahnhofFalkensee.steig_2.ist_verbunden(BahnhofBrieselang.steig_2) should be(true)
  }

  it should "go from Spandau to BahnhofBrieselang" in {
    BahnhofSpandau.steig_3.ist_verbunden(BahnhofBrieselang.steig_2) should be(true)
  }

  it should "go from Spandau to Nauen" in {
    BahnhofSpandau.steig_3.ist_verbunden(BahnhofNauen.steig_2) should be(true)
  }
}

