ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"
assembly / mainClass := Some("fr.charlotte.Main")
assembly / assemblyJarName := "snapshot.jar"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "com.monovore" %% "decline" % "2.3.1",
  "com.lihaoyi" %% "upickle" % "2.0.0",
  "com.lihaoyi" %% "os-lib" % "0.8.1",
  "org.xerial" % "sqlite-jdbc" % "3.39.3.0",
  "commons-io" % "commons-io" % "20030203.000550"
)
lazy val root = (project in file("."))
  .settings(
    name := "STARFinder"
  )
