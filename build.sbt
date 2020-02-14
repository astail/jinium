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

sourceGenerators in Compile += Def.task {
  import scala.sys.process.Process

  val file = (sourceManaged in Compile).value / "net" / "astail" / "Git.scala"
  val longHash = Process("""git log -1 --format="%H"""").!!
  val shortHash = Process("""git log -1 --format="%h"""").!!
  val log = Process("git show -s").!!
  val tag = Process("git rev-parse --verify HEAD").!!
  IO.write(file, s"""package net.astail
                    |
                    |object Git {
                    |  val longHash = $longHash
                    |  val shortHash = $shortHash
                    |  val tag = \"\"\"$tag\"\"\"
                    |  val log = \"\"\"$log\"\"\"
                    |}""".stripMargin)
  Seq(file)
}
