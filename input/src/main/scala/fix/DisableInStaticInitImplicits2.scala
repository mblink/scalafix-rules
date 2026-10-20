/*
rule = DisableInStaticInit
DisableInStaticInit.implicits = [
  {
    id = "clock"
    pattern = "^\\Qfix/DisableInStaticInitImplicits2.\\E(systemClock|Ticks.ticker|Pulse.current)\\Q.\\E$"
  }
]
*/
package fix

object DisableInStaticInitImplicits2 {
  case class Clock(millis: () => Long)

  implicit val systemClock: Clock = Clock(() => System.currentTimeMillis())

  case class Tick(n: Long)
  object Ticks {
    implicit val ticker: Tick = Tick(System.currentTimeMillis())
  }
  import Ticks._

  case class Pulse(n: Long)
  object Pulse {
    implicit val current: Pulse = Pulse(System.currentTimeMillis())
  }

  def now(implicit c: Clock): Long = c.millis()
  def tick(implicit t: Tick): Long = t.n
  def pulse(implicit p: Pulse): Long = p.n

  val a = now/* assert: DisableInStaticInit.clock
          ^^^
  fix/DisableInStaticInitImplicits2.systemClock. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val ticked = tick/* assert: DisableInStaticInit.clock
               ^^^^
  fix/DisableInStaticInitImplicits2.Ticks.ticker. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val pulsed = pulse/* assert: DisableInStaticInit.clock
               ^^^^^
  fix/DisableInStaticInitImplicits2.Pulse.current. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val b = now(Clock(() => 0L))
  def c = now
  val d = () => now
}
