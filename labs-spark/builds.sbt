name := "spark"
version := "0.0.1"
scalaVersion := "2.11.12"

// these settings will be share across subsequent projects
lazy val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.11.12",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.25",
    "org.apache.spark" %% "spark-core" % "2.4.4",
    "org.apache.spark" %% "spark-sql" % "2.4.4"
  ),    
  resolvers ++= Seq(
    // resolver here
    Resolver.mavenLocal,
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  )
)

lazy val root = project.in(file(".")).settings(
  assemblyJarName in assembly := s"spark-${version.value}.jar",
  commonSettings
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {
    _.data.getName == "compile-0.1.0.jar"
  }
}
