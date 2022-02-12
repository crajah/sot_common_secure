import Dependencies._
import sbt.Resolver
import sbt.Keys.{libraryDependencies, publishTo}
import com.amazonaws.regions.{Region, Regions}


lazy val sot_common_secure = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "parallelai",
      scalaVersion := "2.12.4",
      version      := "0.1.1"
    )),
    name := "sot_common_secure",
    s3region := Region.getRegion(Regions.EU_WEST_2),
    publishTo := {
      val prefix = if (isSnapshot.value) "snapshot" else "release"
      Some(s3resolver.value("Parallel AI "+prefix+" S3 bucket", s3(prefix+".repo.parallelai.com")) withMavenPatterns)
    },
    resolvers ++= Seq[Resolver](
      s3resolver.value("Parallel AI S3 Releases resolver", s3("release.repo.parallelai.com")) withMavenPatterns,
      s3resolver.value("Parallel AI S3 Snapshots resolver", s3("snapshot.repo.parallelai.com")) withMavenPatterns
    ),
    resolvers += sbtResolver.value,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "io.spray" %%  "spray-json" % "1.3.4",
    libraryDependencies += "joda-time" % "joda-time" % "2.9.9",
    assemblyMergeStrategy in assembly := {
      case "application.conf"                            => MergeStrategy.concat
      case "project.properties"                          => MergeStrategy.concat
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }

  )

// to run in docker
// docker run -v ~/.config:/root/.config
// --env GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_ladbroke_credentials.json
// -p 8080:8080 parallelai/sot_lcm

enablePlugins(DockerPlugin)

dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    expose(8080)
    volume("/root/.config")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

buildOptions in docker := BuildOptions(cache = false)
