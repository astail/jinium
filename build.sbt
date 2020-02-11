organization := "net.astail"

name := "jinium"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "com.github.slack-scala-client" %% "slack-scala-client" % "0.2.6",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
  "org.skinny-framework" %% "skinny-orm" % "3.0.3",
  "mysql" % "mysql-connector-java" % "8.0.19",
  "commons-codec" % "commons-codec" % "1.13"
)

enablePlugins(JavaAppPackaging)
