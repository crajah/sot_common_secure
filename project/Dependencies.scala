import sbt._

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  val grizzledLogging = "org.clapper" %% "grizzled-slf4j" % "1.3.2"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val sprayJson = "io.spray" %%  "spray-json" % "1.3.4"

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % "0.9.1")

  val jodaTime = "joda-time" % "joda-time" % "2.9.9"

  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.7"
}