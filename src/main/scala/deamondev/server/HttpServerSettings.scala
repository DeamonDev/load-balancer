package deamondev.server

import io.netty.channel.ChannelFactory
import io.netty.channel.ServerChannel
import zhttp.service.EventLoopGroup
import zhttp.service.server.ServerChannelFactory
import zio._

/** This is low level ZIO-HTTP settings layer. Probably, this will be redundant
  * in the future.
  */

object HttpServerSettings {
  type HttpServerSettings = ChannelFactory[ServerChannel] with EventLoopGroup
  lazy val default: ZLayer[Any, Nothing, HttpServerSettings] =
    EventLoopGroup.auto(0) ++ ServerChannelFactory.auto
}
