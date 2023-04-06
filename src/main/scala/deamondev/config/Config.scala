package deamondev.config

import com.typesafe.config.ConfigFactory
import zio.ZIO
import zio.ZLayer
import zio.config._
import zio.config.magnolia.Descriptor
import zio.config.magnolia._
import zio.config.syntax._
import zio.config.typesafe.TypesafeConfigSource.fromTypesafeConfig

object Config {

  case class HttpConfig(host: String, port: Int)

  case class AppConfig(http: HttpConfig, servers: List[String])

  type ConfigEnv = AppConfig

  object AppConfig {
    private lazy val appConfigLayer: ZLayer[Any, Nothing, AppConfig] = ZLayer {
      val getTypesafeConfig = ZIO.attempt(ConfigFactory.load.resolve)
      val getConfig =
        read(descriptor[AppConfig].from(fromTypesafeConfig(getTypesafeConfig)))

      getConfig.orDie
    }

    val live: ZLayer[Any, Nothing, ConfigEnv] =
      ZLayer.make[ConfigEnv](appConfigLayer)
  }

}
