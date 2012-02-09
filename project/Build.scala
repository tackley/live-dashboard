import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "live-dashboard"
    val appVersion      = "1.1"

    val appDependencies = Seq(
      "org.zeromq" %% "zeromq-scala-binding" % "0.0.3",
      "org.scala-tools.time" %% "time" % "0.5",
      "com.gu.openplatform" %% "content-api-client" % "1.13",
      "org.joda" % "joda-convert" % "1.1" % "provided",
      "net.liftweb" %% "lift-json" % "2.4-M4",
      "net.liftweb" %% "lift-json-ext" % "2.4-M4",
      "org.specs2" %% "specs2" % "1.6.1" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
      resolvers ++= Seq(
        "Typesafe Repository (snapshots)" at "http://repo.typesafe.com/typesafe/snapshots/",
        "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"
      )
    )

}
