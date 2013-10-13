name := "restapp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.apache.tika" % "tika-parsers" % "1.4"
)     

play.Project.playScalaSettings
