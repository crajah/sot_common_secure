import scala.language.postfixOps
import Dependencies._
import sbt.Resolver
import sbt.Keys.{libraryDependencies, publishTo}
import com.amazonaws.regions.{Region, Regions}
import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

lazy val scala_2_11 = "2.11.11"
lazy val scala_2_12 = "2.12.5"

lazy val sot_common_secure = (project in file("."))
  .settings(
    name := "sot_common_secure",
    inThisBuild(List(
      organization := "parallelai",
      scalaVersion := scala_2_12
    )),
    promptTheme := com.scalapenos.sbt.prompt.PromptThemes.ScalapenosTheme,
    crossScalaVersions := Seq(scala_2_11, scala_2_12),
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
      commonsLang
    ) ++ circe
  )