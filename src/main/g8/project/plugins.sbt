resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)
resolvers += Classpaths.sbtPluginReleases

libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"

addSbtPlugin("au.com.onegeek"         %% "sbt-dotenv"               % "1.1.36")
addSbtPlugin("si.urbas"                % "sbt-release-notes-plugin" % "0.0.3")
addSbtPlugin("com.github.gseitz"       % "sbt-release"              % "1.0.3")
addSbtPlugin("com.typesafe.sbt"        % "sbt-git"                  % "0.8.5")
addSbtPlugin("de.heikoseeberger"       % "sbt-header"               % "1.6.0")
addSbtPlugin("com.geirsson"            % "sbt-scalafmt"             % "0.4.7")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt"               % "0.3.5")