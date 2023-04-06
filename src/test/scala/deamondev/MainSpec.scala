package deamondev

import zio._
import zio.test._
import zio.test.Assertion._

case object MainSpec extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment with Scope, Any] =
    suite("HelloSpec")(
      test("1 + 1 equals 2") {
        assertZIO(ZIO.succeed(1 + 1))(Assertion.equalTo(2))
      },
      test("2 + 3 equals 5") {
        assertZIO(ZIO.succeed(2 + 3))(Assertion.equalTo(5))
      }
    )
}
