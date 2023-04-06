package deamondev.engine

import zio._
import deamondev.engine.Worker

trait WorkerService {
  def spawnWorker(
      workerId: Int
  ): ZIO[Any, Nothing, Worker] // TODO: add exceptions

  def getWorkerState(workerId: Int): UIO[WorkerState]
  def setWorkerState(workerId: Int, newWorkerState: WorkerState): UIO[Unit]
}

object WorkerService {
  def spawnWorker(workerId: Int): ZIO[WorkerService, Nothing, Worker] =
    ZIO.serviceWithZIO(_.spawnWorker(workerId))
}

case class WorkerServiceLive(workers: Ref[List[Worker]]) extends WorkerService {
  override def spawnWorker(workerId: Int): ZIO[Any, Nothing, Worker] =
    for {
      _ <- ZIO.logInfo("spawning worker...")
      worker = Worker(workerId)
      _ <- workers.update(_ :+ worker)
    } yield worker

  override def getWorkerState(workerId: Int): UIO[WorkerState] =
    for {
      workers <- workers.get
      worker = workers.find(_.id == workerId).get // unsafe
      workerStateR <- worker.workerStateR
      workerState <- workerStateR.get
    } yield workerState

  override def setWorkerState(
      workerId: Int,
      newWorkerState: WorkerState
  ): UIO[Unit] = for {
    workers <- workers.get
    worker = workers.find(_.id == workerId).get // unsafe
    workerStateR <- worker.workerStateR
    _ <- workerStateR.set(newWorkerState)
  } yield ()
}

object WorkerServiceLive {
  def layer: ZLayer[Ref[List[Worker]], Nothing, WorkerService] =
    ZLayer.fromFunction(WorkerServiceLive.apply _)
}
