import scala.language.postfixOps
import Dependencies._
import sbt.Resolver
import sbt.Keys.{libraryDependencies, publishTo}
import com.amazonaws.regions.{Region, Regions}

lazy val scala_2_11 = "2.11.11"
lazy val scala_2_12 = "2.12.4"

lazy val assemblySettings = assemblyMergeStrategy in assembly := {
  case "application.conf" => MergeStrategy.concat
  case "project.properties" => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val sot_common_secure = (project in file("."))
  .settings(
    name := "sot_common_secure",
    inThisBuild(List(
      organization := "parallelai",
      scalaVersion := scala_2_11
    )),
    crossScalaVersions := Seq(scala_2_11, scala_2_12),
    assemblySettings,
    s3region := Region.getRegion(Regions.EU_WEST_2),
    publishTo := {
      val prefix = if (isSnapshot.value) "snapshot" else "release"
      Some(s3resolver.value(s"Parallel AI $prefix S3 bucket", s3(s"$prefix.repo.parallelai.com")) withMavenPatterns)
    },
    resolvers ++= Seq[Resolver](
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      s3resolver.value("Parallel AI S3 Releases resolver", s3("release.repo.parallelai.com")) withMavenPatterns,
      s3resolver.value("Parallel AI S3 Snapshots resolver", s3("snapshot.repo.parallelai.com")) withMavenPatterns
    ),
    resolvers += sbtResolver.value,
    libraryDependencies ++= Seq(
      scalaTest % Test
    ),
    libraryDependencies ++= Seq(
      sprayJson,
      jodaTime,
      grizzledLogging,
      logback,
      upickle
    ) ++ circe
  )