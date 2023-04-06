package deamondev.engine

import zio._
import zio.test._

object LoadBalancerSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = 
    suite("LoadBalancerSpec")(
      test("1 + 1 equals 2") {
        assertTrue(1 + 1 == 2)
      }
    )
}
