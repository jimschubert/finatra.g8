import sbt.Keys._
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.DockerAlias

import scala.language.postfixOps

name := "$name$"

organization := "$organization$"

version := "$service_version$"

scalaVersion := "$scala_version$"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

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

scapegoatVersion := "1.3.1"

scalafmtConfig := file(".scalafmt.conf")
scalafmtOnCompile := true
scalafmtTestOnCompile := true
scalafmtVersion := "1.2.0"

lazy val versions = new {
  val finatra        = "2.13.0"
  val guice          = "4.1.0"
  val logback        = "1.2.3"
  val mockito        = "1.9.5"
  val scalatest      = "3.0.1"
  val junitInterface = "0.11"
  val dockerItScala  = "0.9.4"
  val scalaUri       = "0.4.16"
  val hamsters       = "1.4.1"
  val fluentdScala   = "0.2.5"
  val swaggerFinatra = "2.12.0"
  val wireMock       = "2.8.0"
  val catbird        = "0.16.0"
}

libraryDependencies ++= Seq(
  "io.catbird"                   %% "catbird-finagle"      % versions.catbird,
  "com.github.tomakehurst"       % "wiremock"              % versions.wireMock,
  "com.jakehschwartz"            % "finatra-swagger_2.12"  % versions.swaggerFinatra,
  "eu.inn"                       %% "fluentd-scala"        % versions.fluentdScala,
  "io.github.scala-hamsters"     %% "hamsters"             % versions.hamsters,
  "com.netaporter"               %% "scala-uri"            % versions.scalaUri,
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
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-deprecation",
    "-explaintypes",
    "-feature",
    "-Xcheckinit",
    "-Xfuture",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused",
    "-Ypartial-unification",
    "-P:clippy:colors=true"
)

// bashScriptExtraDefines += """addJava "-Dnetworkaddress.cache.ttl=60""""
bashScriptExtraDefines ++= Seq("""addJava "-Dnetworkaddress.cache.ttl=60"""",
                               """addJava "-XX:+UnlockExperimentalVMOptions"""",
                               """addJava "-XX:+UseCGroupMemoryLimitForHeap"""")
bashScriptExtraDefines ++= Seq("""addApp "-log.level=$"$"${LOG_LEVEL:-INFO}"""",
                               s"""addApp "-service.version=$"$"${version.value}"""")

val gitHeadCode = SettingKey[String]("git-head-hash", "The commit hash code of HEAD")
gitHeadCode := git.gitHeadCommit.value.map { sha => s"$"$"${sha.take(7)}" }.getOrElse("na")

defaultLinuxInstallLocation in Docker := "/opt/$docker_package_name$"
packageName in Docker := "vr/$docker_package_name$"
dockerBaseImage := "openjdk:8-jre-alpine"
version in Docker := s"$"$"${if (gitHeadCode.value != "na") s"$"$"${version.value}_$"$"${gitHeadCode.value}" else version.value}"
maintainer in Docker := "$maintainer_name$ <$maintainer_email$>"
dockerExposedPorts := Seq(9999, 9990)
dockerRepository := Some("$docker_repository$")
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
