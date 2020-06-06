addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.0")
addSbtPlugin("com.dwolla.sbt"   % "docker-containers"   % "1.3.0")

// libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

resolvers ++= Seq(
  Resolver.bintrayIvyRepo("dwolla", "sbt-plugins"),
  Resolver.bintrayIvyRepo("dwolla", "maven")
)
