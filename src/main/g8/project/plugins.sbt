resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)
resolvers += Classpaths.sbtPluginReleases

libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"

addSbtPlugin("au.com.onegeek"         %% "sbt-dotenv"               % "1.1.36")
addSbtPlugin("si.urbas"                % "sbt-release-notes-plugin" % "0.0.3")
addSbtPlugin("com.github.gseitz"       % "sbt-release"              % "1.0.3")
addSbtPlugin("com.typesafe.sbt"        % "sbt-git"                  % "0.8.5")
addSbtPlugin("de.heikoseeberger"       % "sbt-header"               % "1.6.0")
addSbtPlugin("com.geirsson"            % "sbt-scalafmt"             % "0.6.3")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt"               % "0.5.3")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat"            % "1.0.4")
addSbtPlugin("org.scoverage"           % "sbt-scoverage"            % "1.5.0")
addSbtPlugin("si.urbas"                % "sbt-release-notes-plugin" % "0.0.3")
addSbtPlugin("com.47deg"               % "sbt-microsites"           % "0.5.3")
addSbtPlugin("com.lucidchart"          % "sbt-scalafmt"             % "1.8")
addSbtPlugin("org.duhemm"              % "sbt-errors-summary"       % "0.3.0")
// addSbtPlugin("io.get-coursier"         % "sbt-coursier"             % "1.0.0-M15-1")
