package deamondev.engine

sealed trait WorkerState
case object Waiting extends  WorkerState
case object Working extends WorkerState
