import sbt.Keys._
import com.typesafe.sbt.packager.docker._
import org.scalafmt.sbt.ScalaFmtPlugin

name := "$name$"

organization := "$organization$"

version := "$service_version$"

scalaVersion := "$scala_version$"

scalaOrganization := "org.typelevel"

fork in run := true

resolvers += Resolver.sonatypeRepo("releases")

resolvers += "maven.twttr.com" at "https://maven.twttr.com"

Revolver.settings

enablePlugins(AutomateHeaderPlugin,
              JavaAppPackaging,
              DockerPlugin,
              GitVersioning,
              GitBranchPrompt,
              MdReleaseNotesFormat,
              RootFolderReleaseNotesStrategy)

initialCommands in console := """
                | import com.twitter.util.{Future, FuturePool, Await}
                |""".stripMargin

ScalaFmtPlugin.autoImport.reformatOnCompileSettings
ScalaFmtPlugin.autoImport.scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf")

lazy val versions = new {
  val finatra        = "2.6.0"
  val guice          = "4.1.0"
  val logback        = "1.1.7"
  val mockito        = "1.9.5"
  val scalatest      = "3.0.1"
  val junitInterface = "0.11"
  val mongoScala     = "1.2.1"
  val swaggerCore    = "1.5.10"
  val swaggerScala   = "1.0.2"
  val swaggerUI      = "2.2.6"
  val dockerItScala  = "0.9.0-M10"
  val scalaUri       = "0.4.16"
  val hamsters       = "1.0.7"
  val errors         = "1.1"
  val fluentdScala   = "0.1.21"
}

libraryDependencies ++= Seq(
  "eu.inn"                       %% "fluentd-scala"        % versions.fluentdScala,
  "com.github.mehmetakiftutuncu" %% "errors"               % versions.errors,
  "io.github.scala-hamsters"     %% "hamsters"             % versions.hamsters,
  "com.netaporter"               %% "scala-uri"            % versions.scalaUri,
  "io.swagger"                   % "swagger-core"          % versions.swaggerCore,
  "io.swagger"                   %% "swagger-scala-module" % versions.swaggerScala,
  "org.webjars"                  % "swagger-ui"            % versions.swaggerUI,
  "com.twitter"                  %% "finatra-http"         % versions.finatra,
  "com.twitter"                  %% "finatra-httpclient"   % versions.finatra,
  "com.twitter"                  %% "finatra-jackson"      % versions.finatra,
  "ch.qos.logback"               % "logback-classic"       % versions.logback,
  "org.mongodb.scala"            %% "mongo-scala-driver"   % versions.mongoScala,
  "com.twitter"                  %% "finatra-http"    % versions.finatra % "test",
  "com.twitter"                  %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter"                  %% "inject-server"   % versions.finatra % "test",
  "com.twitter"                  %% "inject-app"      % versions.finatra % "test",
  "com.twitter"                  %% "inject-core"     % versions.finatra % "test",
  "com.twitter"                  %% "inject-modules"  % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib"    % versions.guice   % "test",
  "com.twitter" %% "finatra-http"    % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-server"   % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-app"      % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-core"     % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-modules"  % versions.finatra % "test" classifier "tests",
  "org.mockito"   % "mockito-core"                 % versions.mockito        % "test",
  "org.scalatest" %% "scalatest"                   % versions.scalatest      % "test",
  "com.novocode"  % "junit-interface"              % versions.junitInterface % "test",
  "com.whisk"     %% "docker-testkit-scalatest"    % versions.dockerItScala  % "test",
  "com.whisk"     %% "docker-testkit-impl-spotify" % versions.dockerItScala  % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ypartial-unification",
  "-Yliteral-types"
)

bashScriptExtraDefines ++= Seq("""addApp "-log.level=$"$"${LOG_LEVEL}"""",
                               s"""addApp "-service.version=$"$"${version.value}"""")

val gitHeadCode = SettingKey[String]("git-head-hash", "The commit hash code of HEAD")
gitHeadCode := git.gitHeadCommit.value.map { sha => s"$"$"${sha.take(7)}" }.getOrElse("na")

defaultLinuxInstallLocation in Docker := "/opt/$docker_package_name$"
packageName in Docker := "vr/$docker_package_name$"
dockerBaseImage := "java:8-jre-alpine"
version in Docker := s"$"$"${version.value}_$"$"${gitHeadCode.value}"
maintainer in Docker := "Richard Chuo <richard_chuo@htc.com>"
dockerExposedPorts := Seq(9999, 9990)
dockerRepository := Some("vr-docker-registry-usw2.cshtc-vr.com")
dockerCommands := dockerCommands.value.take(1) ++ Seq(
  Cmd("LABEL", s"version=$"$"${version.value}"),
  Cmd("ENV", "SERVICE_NAME=$docker_package_name$ SERVICE_TAGS=$service_tags$"),
  Cmd("RUN", """if test -f /etc/alpine-release; then apk update --no-progress && apk upgrade -v;fi"""),
  Cmd("RUN", """if test -f /etc/alpine-release; then apk add bash;fi""")
) ++ dockerCommands.value.drop(1)
