name := "spark"
version := "0.0.1"
scalaVersion := "2.11.12"

// these settings will be share across subsequent projects
lazy val commonSettings = Seq(
  version := "0.0.1",
  scalaVersion := "2.11.12",
  dependencyOverrides ++= Seq(
    "net.jpountz.lz4" % "lz4" % "1.3.0"
  ),
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "2.3.0",
    "org.apache.spark" %% "spark-sql" % "2.3.0",
    "org.apache.spark" %% "spark-streaming" % "2.3.0",
    "org.apache.spark" %% "spark-streaming-kafka" % "1.6.3",
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0",
    "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.0"
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
