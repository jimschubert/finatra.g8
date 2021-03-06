import sbt.Keys._

name := "$name$"

organization := "$organization$"

version := "$version$"

scalaVersion := "$scala_version$"

fork in run := true

resolvers += Resolver.sonatypeRepo("releases")

resolvers += "maven.twttr.com" at "https://maven.twttr.com"

Revolver.settings

enablePlugins(JavaAppPackaging)

initialCommands in console := """
                | import com.twitter.util.{Future, FuturePool, Await}
                |""".stripMargin

lazy val versions = new {
  val finatra = "2.5.0"
  val guice = "4.0"
  val logback = "1.1.7"
  val mockito = "1.9.5"
  val scalatest = "2.2.6"
  val junitInterface = "0.11"
}

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "com.twitter" %% "finatra-httpclient" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,

  "com.twitter" %% "finatra-http" % versions.finatra % "test",
  "com.twitter" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter" %% "inject-server" % versions.finatra % "test",
  "com.twitter" %% "inject-app" % versions.finatra % "test",
  "com.twitter" %% "inject-core" % versions.finatra % "test",
  "com.twitter" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % versions.mockito % "test",
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  "com.novocode" % "junit-interface" % versions.junitInterface % "test"
  )

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8", 
  "-Xlint", 
  "-Yclosure-elim",
  "-Yinline", 
  "-Xverify",
  "-feature",
  "-language:postfixOps"
)
