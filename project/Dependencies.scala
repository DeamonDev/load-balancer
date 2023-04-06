import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"

  val DoobieVersion = "1.0.0-RC2"

  val LogbackVersion = "1.2.11"
  val LogbackEncoderVersion = "4.11"

  val tapirVersion = "1.1.0"

  val ZIOVersion = "2.0.1"
  val ZIOConfigVersion = "3.0.1"
  val ZIOLoggingVersion = "2.0.1"
  val ZIOTestContainersVersion = "0.8.0"
  val zioQuillVersion = "4.4.1"
  val postgresVersion = "42.3.6"
  val slf4jVersion = "2.0.5"

  lazy val logging = Seq(
    "org.slf4j" % "slf4j-api" % "2.0.0",
    "org.slf4j" % "slf4j-simple" % "2.0.0"
    // "dev.zio" %% "zio-logging-slf4j" % "2.1.3"
    // "org.slf4j" % "log4j-over-slf4j" % "2.0.3"
  )

  lazy val database = Seq(
    "io.getquill" %% "quill-jdbc-zio" % zioQuillVersion,
    "org.postgresql" % "postgresql" % postgresVersion,
    "org.flywaydb" % "flyway-core" % "6.1.0",
  )

  lazy val quill = Seq("io.getquill" %% "quill-jdbc-zio" % zioQuillVersion)

  lazy val mail = Seq("javax.mail" % "mail" % "1.4.7")

  lazy val crypto = Seq("com.github.t3hnar" %% "scala-bcrypt" % "4.3.0")

  lazy val cats = Seq("org.typelevel" %% "cats-core" % "2.8.0")

  lazy val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion
  )

  lazy val jwt = Seq("com.github.jwt-scala" %% "jwt-zio-json" % "9.1.1")

  lazy val sttp =
    Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.8.3",
      "com.softwaremill.sttp.client3" %% "zio" % "3.8.3",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % "1.2.1"
    )

  lazy val zio = Seq(
    "dev.zio" %% "zio-http" % "0.0.5",
    "dev.zio" %% "zio" % ZIOVersion,
    "io.github.scottweaver" %% "zio-2-0-db-migration-aspect" % ZIOTestContainersVersion,
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % ZIOTestContainersVersion,
    "dev.zio" %% "zio-config" % ZIOConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % ZIOConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % ZIOConfigVersion,
    "dev.zio" %% "zio-interop-cats" % "3.3.0",
    "dev.zio" %% "zio-json" % "0.3.0-RC10",
    "dev.zio" %% "zio-mock" % "1.0.0-RC8",
    "dev.zio" %% "zio-prelude" % "1.0.0-RC15",
    "dev.zio" %% "zio-streams" % ZIOVersion,
    "dev.zio" %% "zio-test" % "2.0.10" % Test,
    "dev.zio" %% "zio-test-magnolia" % "2.0.10" % Test,
    "dev.zio" %% "zio-test-sbt" % "2.0.10" % Test
  )

  lazy val dependencies = database ++ logging ++ tapir ++ zio ++ quill ++ jwt ++ mail ++ cats ++ crypto ++ sttp
}