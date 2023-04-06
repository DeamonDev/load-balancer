package deamondev.engine

import zio._

final case class Worker(id: Int) {
  val workerStateR: UIO[Ref[WorkerState]] = Ref.make[WorkerState](Waiting)
}
