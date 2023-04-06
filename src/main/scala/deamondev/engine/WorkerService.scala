package deamondev.engine

import zio._
import deamondev.engine.Worker

trait WorkerService {
  def spawnWorker(
      workerId: Int
  ): ZIO[Any, Nothing, Worker] // TODO: add exceptions
}

object WorkerService {
  def spawnWorker(workerId: Int): ZIO[WorkerService, Nothing, Worker] =
    ZIO.serviceWithZIO(_.spawnWorker(workerId))
}

case class WorkerServiceLive(workers: Ref[List[Worker]]) extends WorkerService {
  override def spawnWorker(workerId: Int): ZIO[Any, Nothing, Worker] =
    ZIO.logInfo("spawning worker...") *> 
      ZIO.succeed(Worker(workerId))
}

object WorkerServiceLive {
  def layer: ZLayer[Ref[List[Worker]], Nothing, WorkerService] =
    ZLayer.fromFunction(WorkerServiceLive.apply _)
}
