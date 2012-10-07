import sbt._
import sbt.Keys._

object CellularautomatonBuild extends Build {

  lazy val cellularautomaton = Project(
    id = "cellularautomaton",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "cellularautomaton",
      organization := "pityu",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.3",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

      // add other settings here

    )
  )
}
