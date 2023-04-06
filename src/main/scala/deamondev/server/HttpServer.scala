package deamondev.server

import deamondev.engine.LoadBalancer
import sttp.client3
import zhttp.http.Http
import zhttp.http.HttpApp
import zhttp.http.Method
import zhttp.http.Middleware
import zhttp.http.Request
import zhttp.http.Response
import zhttp.http.Status
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Client
import zio._

trait HttpServer {
  def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]]
}

object HttpServer {
  def httpApp: ZIO[HttpServer, Nothing, HttpApp[Any, Throwable]] =
    ZIO.serviceWithZIO[HttpServer](_.httpApp)
}

case class HttpServerLive(
    loadBalancer: LoadBalancer
) extends HttpServer {

  val app: HttpApp[Any, Throwable] = Http.collectZIO[Request] {
    case req: Request =>
      loadBalancer
        .balance(req)
  }

  val corsSettings: CorsConfig = CorsConfig(anyOrigin = true, anyMethod = true)
  override def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]] =
    ZIO.succeed(
      app @@ Middleware.debug @@ Middleware.cors(
        corsSettings
      )
    )
}

object HttpServerLive {
  def layer: ZLayer[
    LoadBalancer,
    Nothing,
    HttpServer
  ] =
    ZLayer.fromFunction(HttpServerLive.apply _)
}
