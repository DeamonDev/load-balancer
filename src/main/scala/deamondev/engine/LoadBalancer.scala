package deamondev.engine

import deamondev.config.Config
import deamondev.server.HttpServerSettings
import io.netty.channel.ServerChannel
import sttp.client3.SttpBackend
import sttp.model.Uri
import zhttp.http
import zhttp.http.Request
import zhttp.service.ChannelFactory
import zhttp.service.Client
import zhttp.service.EventLoopGroup
import zhttp.service.server.ServerChannelFactory
import zio._
import zio.http.URL

import scala.collection.mutable

trait LoadBalancer {
  def balance(req: Request): ZIO[Any, Throwable, http.Response]
  def start(): ZIO[Any, Nothing, Unit]
}

object LoadBalancer {
  def balance(req: Request): ZIO[LoadBalancer, Throwable, http.Response] =
    ZIO.serviceWithZIO(_.balance(req))

  def start(): ZIO[LoadBalancer, Nothing, Unit] =
    ZIO.serviceWithZIO(_.start())
}

case class LoadBalancerLive(
    appConfig: Config.AppConfig,
    requests: Queue[Request],
    responsePromisesR: Ref[
      mutable.Map[String, Promise[Nothing, http.Response]]
    ],
    workerService: WorkerService
) extends LoadBalancer {

  private val env = EventLoopGroup.auto(0) ++ ChannelFactory.auto
  private val availableServers = appConfig.servers
  private val nAvailableServers = availableServers.length

  private def chooseNextServer(lastlyUsedServerIdR: Ref[Int]): UIO[Int] =
    lastlyUsedServerIdR.updateAndGet(id => (id + 1) % nAvailableServers)

  private def doWork(
      worker: Worker,
      lastlyUsedServerIdR: Ref[Int]
  ): ZIO[Any, Nothing, Unit] =
    for {
      _ <- ZIO.logInfo(
        s"[worker-${worker.id}] waiting for something to work on..."
      )
      req <- requests.take
      _ <- ZIO.logInfo(s"[worker-${worker.id}] working...")
      reqHash <- computeRequestHash(req)
      promise <- responsePromisesR.get.map(m => m(reqHash))
      nextServerId <- chooseNextServer(lastlyUsedServerIdR)
      destinationServer = availableServers(nextServerId)
      res <- performRequest(req, destinationServer).orDie
      _ <- promise.succeed(res)
      _ <- responsePromisesR.update(_ -= reqHash)
      _ <- ZIO.sleep(Duration.fromMillis(500))
      _ <- doWork(worker, lastlyUsedServerIdR)
    } yield ()

  private def computeRequestHash(req: Request): ZIO[Any, Nothing, String] =
    ZIO.succeed(req.method.toString() ++ "#" ++ req.path.toString())

  private def performRequest(
      req: Request,
      server: String
  ): ZIO[Any, Throwable, http.Response] =
    Client
      .request(
        req.copy(url =
          http.URL
            .fromString(s"${server}${req.path.toString()}")
            .getOrElse(req.url)
        ),
        Client.Config.empty
      )
      .provide(env)

  override def balance(req: Request): ZIO[Any, Throwable, http.Response] =
    for {
      promise <- Promise.make[Nothing, http.Response]
      reqHash <- computeRequestHash(req) // GET#/api/v1/books
      _ <- responsePromisesR.update(m => m + (reqHash -> promise))
      _ <- requests.offer(req)
      res <- promise.await
      _ <- ZIO.logInfo("finally taken value...")
    } yield res

  override def start(): ZIO[Any, Nothing, Unit] =
    for {
      lastlyUsedServerIdR <- Ref.make[Int](0)

      fibs <- ZIO.collectAll(
        (1 to 100).map(i =>
          workerService
            .spawnWorker(i)
            .flatMap(worker => doWork(worker, lastlyUsedServerIdR).fork)
        )
      )

      _ <- ZIO.collectAll(fibs.map(fib => fib.await))
    } yield ()
}

object LoadBalancerLive {
  def layer: ZLayer[
    Config.AppConfig
      with Queue[Request]
      with Ref[mutable.Map[String, Promise[Nothing, http.Response]]]
      with WorkerService,
    Throwable,
    LoadBalancer
  ] =
    ZLayer.fromFunction(LoadBalancerLive.apply _)
}
