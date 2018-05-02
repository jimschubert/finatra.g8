resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("au.com.onegeek"         %% "sbt-dotenv"               % "1.2.88")
addSbtPlugin("com.github.gseitz"       % "sbt-release"              % "1.0.7")
addSbtPlugin("com.typesafe.sbt"        % "sbt-git"                  % "0.9.3")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt"               % "0.5.3")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat"            % "1.0.9")
addSbtPlugin("org.scoverage"           % "sbt-scoverage"            % "1.5.1")
addSbtPlugin("com.47deg"               % "sbt-microsites"           % "0.7.16")
addSbtPlugin("com.lucidchart"          % "sbt-scalafmt"             % "1.15")
addSbtPlugin("org.duhemm"              % "sbt-errors-summary"       % "0.6.0")
addSbtPlugin("com.birdhowl"            % "sbt-mfinger"              % "0.1.0")
addSbtPlugin("com.timushev.sbt"        % "sbt-updates"              % "0.3.4")
addSbtPlugin("com.mintbeans"           % "sbt-ecr"                  % "0.9.0")
// addSbtPlugin("io.get-coursier"         % "sbt-coursier"             % "1.0.0-M15-1")
// addSbtPlugin("si.urbas"                % "sbt-release-notes-plugin" % "0.0.3")
// addSbtPlugin("com.geirsson"            % "sbt-scalafmt"             % "0.6.3")
