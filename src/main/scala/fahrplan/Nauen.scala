package fahrplan

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
    val spandau_auswärts = GeradeStrecke.auto_Abschnitte(160, BahnhofSpandau.abstand(BahnhofFalkensee), 1000)
    val spandau_einwärts = GeradeStrecke.auto_Abschnitte(160, BahnhofSpandau.abstand(BahnhofFalkensee), 1000)
    spandau_auswärts.plus_Gleiswechsel(spandau_einwärts) // auf Falkenseer Seite
    spandau_einwärts.plus_Gleiswechsel(spandau_auswärts) // auf Spandauer Seite

    spandau_einwärts ++> List(BahnhofSpandau.steig_1, BahnhofSpandau.steig_2)
    spandau_einwärts <++ List(BahnhofFalkensee.steig_1, BahnhofFalkensee.durch_1)

    spandau_auswärts <++ List(BahnhofSpandau.steig_3, BahnhofSpandau.steig_4)
    spandau_auswärts ++> List(BahnhofFalkensee.durch_2, BahnhofFalkensee.steig_2)

    val fb_1 = GeradeStrecke.auto_Abschnitte(160, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_2 = GeradeStrecke.auto_Abschnitte(200, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_3 = GeradeStrecke.auto_Abschnitte(200, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)
    val fb_4 = GeradeStrecke.auto_Abschnitte(160, BahnhofFalkensee.abstand(BahnhofBrieselang), 1000)

    fb_1 <== BahnhofFalkensee.steig_1; fb_1 ==> BahnhofBrieselang.steig_1
    fb_2 <== BahnhofFalkensee.durch_1; fb_2 ==> BahnhofBrieselang.durch_1
    fb_3 <== BahnhofBrieselang.durch_2; fb_3 ==> BahnhofFalkensee.durch_2
    fb_4 <== BahnhofBrieselang.steig_2; fb_4 ==> BahnhofFalkensee.steig_2

    val bn_1 = GeradeStrecke.auto_Abschnitte(200, BahnhofBrieselang.abstand(BahnhofNauen), 1500)
    val bn_2 = GeradeStrecke.auto_Abschnitte(200, BahnhofBrieselang.abstand(BahnhofNauen), 1500)
    bn_1.plus_Gleiswechsel(bn_2) // vor Nauen

    bn_1 <== BahnhofBrieselang.durch_1; bn_1 ==> BahnhofNauen.durchgang_nach_Hamburg
    bn_1 <+ BahnhofBrieselang.steig_1; bn_1 ++> List(BahnhofNauen.steig_1, BahnhofNauen.steig_2)

    bn_2 <== BahnhofNauen.durchgang_von_Hamburg; bn_2 ==> BahnhofBrieselang.durch_2
    bn_2 <++ List(BahnhofNauen.steig_1, BahnhofNauen.steig_2, BahnhofNauen.steig_4, BahnhofNauen.steig_5, BahnhofNauen.durchgang_6)
    bn_2 +> BahnhofBrieselang.steig_2
  }

}
