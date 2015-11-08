version := "0.1.0"

scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.40-R8",
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test",
  "com.github.matey-jack" % "enabler" % "0.1-SNAPSHOT",
  "codes.reactive" %% "scala-time" % "0.1.0-RC1",
  "net.ruippeixotog" %% "scala-scraper" % "0.1.2",
  "org.json4s" %% "json4s-jackson" % "3.3.0"
  // "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  // "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
)

mainClass in (Compile,run) := Some("fahrplan.crawler.ScrapeAllStations") // Some("fahrplan.ui.Gleisbelegung")