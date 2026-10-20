/*
rule = DisableInStaticInit
DisableInStaticInit.implicits = [
  {
    id = "clock"
    pattern = "^\\Qfix/DisableInStaticInitImplicits.\\E(systemClock|Ticks.ticker|Pulse.current|tagged\\(\\))\\Q.\\E$"
  }
]
*/
package fix

object DisableInStaticInitImplicits {
  case class Clock(millis: () => Long)
  case class Zone(id: String)
  case class Tick(n: Long)
  case class Tagged[A](n: Long)
  case class Pulse(n: Long)
  object Pulse {
    given current: Pulse = Pulse(System.currentTimeMillis())
  }

  given systemClock: Clock = Clock(() => System.currentTimeMillis())
  given utc: Zone = Zone("UTC")
  given tagged[A]: Tagged[A] = Tagged(System.currentTimeMillis())

  object Ticks {
    given ticker: Tick = Tick(System.currentTimeMillis())
  }
  import Ticks.given

  def now(using c: Clock): Long = c.millis()
  def nowIn(offset: Long)(using c: Clock): Long = c.millis() + offset
  def zoneId(using z: Zone): String = z.id
  def tick(using t: Tick): Long = t.n
  def tagOf[A](using t: Tagged[A]): Long = t.n
  def pulse(using p: Pulse): Long = p.n

  val a = now/* assert: DisableInStaticInit.clock
          ^^^
  fix/DisableInStaticInitImplicits.systemClock. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  lazy val b = nowIn(1L)/* assert: DisableInStaticInit.clock
               ^^^^^^^^^
  fix/DisableInStaticInitImplicits.systemClock. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val ticked = tick/* assert: DisableInStaticInit.clock
               ^^^^
  fix/DisableInStaticInitImplicits.Ticks.ticker. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val taggedNow = tagOf[Int]/* assert: DisableInStaticInit.clock
                  ^^^^^^^^^^
  fix/DisableInStaticInitImplicits.tagged(). is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val pulsed = pulse/* assert: DisableInStaticInit.clock
               ^^^^^
  fix/DisableInStaticInitImplicits.Pulse.current. is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val c = zoneId
  val d = now(using Clock(() => 0L))
  def e = now
  val f = () => nowIn(2L)

  class C {
    val g = now
  }
}
