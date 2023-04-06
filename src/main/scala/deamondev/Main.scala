package deamondev

import deamondev.config.Config._
import deamondev.engine.LoadBalancer
import deamondev.engine.LoadBalancerLive
import deamondev.server.HttpServer
import deamondev.server.HttpServerLive
import deamondev.server.HttpServerSettings
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import org.flywaydb.core.Flyway
import sttp.client3.Response
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zhttp.http
import zhttp.http.Request
import zhttp.service.Server
import zio.Console._
import zio._

import java.io.IOException
import scala.collection.mutable
import deamondev.engine.WorkerServiceLive
import deamondev.engine.Worker

object Main extends ZIOAppDefault {

  val myAppLogic = ZIO.scoped {
    for {
      config <- ZIO.service[AppConfig]
      _ <- ZIO.logInfo(config.toString())

      loadBalancer <- ZIO.service[LoadBalancer]
      _ <- loadBalancer.start().fork

      http <- ZIO.service[HttpServer]
      httpApp <- http.httpApp

      start <- Server(httpApp)
        .withBinding(config.http.host, config.http.port)
        .make
        .orDie
      _ <- ZIO.logInfo(s"Server started on port: ${start.port}")
      _ <- ZIO.never
    } yield ()
  }

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    myAppLogic.provide(
      AppConfig.live,
      HttpServerLive.layer,
      LoadBalancerLive.layer,
      ZLayer.fromZIO(Queue.bounded[Request](100)),
      ZLayer.fromZIO(
        Ref.make(mutable.Map[String, Promise[Nothing, http.Response]]())
      ),
      HttpServerSettings.default,
      WorkerServiceLive.layer,
      ZLayer.fromZIO(Ref.make(List[Worker]()))
    )
}
