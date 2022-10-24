ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

mainClass in (assembly) := Some("fr.charlotte.run")
assemblyJarName in assembly := "snapshot.jar"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.xerial" % "sqlite-jdbc" % "3.39.3.0"
)
lazy val root = (project in file("."))
  .settings(
    name := "STARFinder"
  )
