version := "0.1.0"

scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.40-R8",
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test",
  "com.github.matey-jack" % "enabler" % "0.1-SNAPSHOT",
  "codes.reactive" %% "scala-time" % "0.1.0-RC1"
)

mainClass in (Compile,run) := Some("fahrplan.ui.Gleisbelegung")