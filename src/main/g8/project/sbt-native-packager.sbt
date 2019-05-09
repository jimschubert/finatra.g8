addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.21")
addSbtPlugin("com.dwolla.sbt"   % "docker-containers"   % "1.2.12")

// libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

resolvers ++= Seq(
  Resolver.bintrayIvyRepo("dwolla", "sbt-plugins"),
  Resolver.bintrayIvyRepo("dwolla", "maven")
)
