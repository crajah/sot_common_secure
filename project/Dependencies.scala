import sbt._

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  val grizzledLogging = "org.clapper" %% "grizzled-slf4j" % "1.3.2"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val sprayJson = "io.spray" %%  "spray-json" % "1.3.4"

  val jodaTime = "joda-time" % "joda-time" % "2.9.9"

  val upickle = "com.lihaoyi" %% "upickle" % "0.5.1"
}