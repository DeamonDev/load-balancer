import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.deamondev"
ThisBuild / organizationName := "deamondev"

lazy val root = (project in file("."))
  .settings(
    name := "backend",
    libraryDependencies ++= dependencies,
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
