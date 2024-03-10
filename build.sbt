val scala3Version = "3.4.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "aes_scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test, // for munit
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
  )
    
assembly / assemblyJarName := "aes.jar"
// assembly / assemblyJarName := "aes.jar"