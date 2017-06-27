import sbt.Keys._
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.DockerAlias

import scala.language.postfixOps

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
              RootFolderReleaseNotesStrategy,
              MicrositesPlugin)

initialCommands in console := """
                | import com.twitter.util.{Future, FuturePool, Await}
                |""".stripMargin

coverageHighlighting := true

scapegoatVersion := "1.3.0"

lazy val scalafmtTask = taskKey[Unit]("run scalafmt")
scalafmtTask := {
  import sys.process._

  Seq("sbt", "scalafmt") !
}
(compile in Compile) <<= (compile in Compile) dependsOn scalafmtTask

lazy val versions = new {
  val finatra        = "2.11.0"
  val guice          = "4.1.0"
  val logback        = "1.2.3"
  val mockito        = "1.9.5"
  val scalatest      = "3.0.1"
  val junitInterface = "0.11"
  val swaggerCore    = "1.5.13"
  val swaggerScala   = "1.0.3"
  val swaggerUI      = "2.2.6"
  val dockerItScala  = "0.9.3"
  val scalaUri       = "0.4.16"
  val hamsters       = "1.4.0"
  val fluentdScala   = "0.2.5"
  val swaggerFinatra = "2.10.0"
  val wireMock       = "2.6.0"
}

libraryDependencies ++= Seq(
  "com.github.tomakehurst"       % "wiremock"              % versions.wireMock,
  "com.jakehschwartz"            % "finatra-swagger_2.12"  % versions.swaggerFinatra,
  "eu.inn"                       %% "fluentd-scala"        % versions.fluentdScala,
  "io.github.scala-hamsters"     %% "hamsters"             % versions.hamsters,
  "com.netaporter"               %% "scala-uri"            % versions.scalaUri,
  "io.swagger"                   % "swagger-core"          % versions.swaggerCore,
  "io.swagger"                   %% "swagger-scala-module" % versions.swaggerScala,
  "org.webjars"                  % "swagger-ui"            % versions.swaggerUI,
  "com.twitter"                  %% "finatra-http"         % versions.finatra,
  "com.twitter"                  %% "finatra-httpclient"   % versions.finatra,
  "com.twitter"                  %% "finatra-jackson"      % versions.finatra,
  "ch.qos.logback"               % "logback-classic"       % versions.logback,
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

fork in test := false

parallelExecution in Test := false

clippyColorsEnabled := true

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
  "-Yliteral-types",
  "-P:clippy:colors=true"
)

bashScriptExtraDefines += """addJava "-Dnetworkaddress.cache.ttl=60""""
bashScriptExtraDefines ++= Seq("""addApp "-log.level=$"$"${LOG_LEVEL}"""",
                               s"""addApp "-service.version=$"$"${version.value}"""")

val gitHeadCode = SettingKey[String]("git-head-hash", "The commit hash code of HEAD")
gitHeadCode := git.gitHeadCommit.value.map { sha => s"$"$"${sha.take(7)}" }.getOrElse("na")

defaultLinuxInstallLocation in Docker := "/opt/$docker_package_name$"
packageName in Docker := "vr/$docker_package_name$"
dockerBaseImage := "java:8-jre-alpine"
version in Docker := s"$"$"${version.value}_$"$"${gitHeadCode.value}"
maintainer in Docker := "$maintainer_name$ <$maintainer_email$>"
dockerExposedPorts := Seq(9999, 9990)
dockerRepository := Some("vr-docker-registry-usw2.cshtc-vr.com")
dockerAlias := DockerAlias(dockerRepository.value,
                           None,
                           (packageName in Docker).value,
                           Some((version in Docker).value))
dockerUpdateLatest := false
dockerBuildOptions := Seq(
  "--force-rm",
  "-t",
  s"$"$"${dockerRepository.value.get}/$"$"${(packageName in Docker).value}:$"$"${(version in Docker).value}",
  "--squash",
  "--no-cache",
  "--pull"
)
dockerCommands := dockerCommands.value.take(1) ++ Seq(
  Cmd("LABEL", s"version=$"$"${version.value}"),
  Cmd("ENV", "SERVICE_NAME=$docker_package_name$ SERVICE_TAGS=$service_tags$"),
  Cmd("RUN", """if test -f /etc/alpine-release; then apk update --no-progress && apk upgrade -v;fi"""),
  Cmd("RUN", """if test -f /etc/alpine-release; then apk add bash;fi""")
) ++ dockerCommands.value.drop(1)

// MicroSites
micrositeName := "$name$"
micrositeBaseUrl := "$microsite_base_url$"
micrositeDocumentationUrl := "/$microsite_base_url$/docs"
micrositeAuthor := "$maintainer_name$"
micrositeOrganizationHomepage := "http://www.htc.com"
micrositeGitHostingService := Other("HICHub")
micrositeGitHostingUrl := "https://hichub.htc.com"
