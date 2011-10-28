seq(webSettings :_*)

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository (snapshots)" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
    "org.zeromq" %% "zeromq-scala-binding" % "0.0.1-SNAPSHOT",
    "se.scalablesolutions.akka" % "akka-actor" % "1.2",
    "org.scala-tools.time" %% "time" % "0.5",
    "org.slf4j" % "slf4j-simple" % "1.6.1",
    "net.liftweb" %% "lift-webkit" % "2.4-M4",
    "org.specs2" %% "specs2" % "1.6.1" % "test",
    "org.eclipse.jetty" % "jetty-webapp" % "7.5.3.v20111011" % "container"
)

port in container.Configuration := 8081


