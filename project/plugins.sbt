resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.16.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

addSbtPlugin("com.scalapenos" % "sbt-prompt" % "1.0.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")