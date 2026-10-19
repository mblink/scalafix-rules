/*
rule = DisableInStaticInit
DisableInStaticInit.implicits = [
  {
    id = "clock"
    pattern = "^\\Qfix/DisableInStaticInitImplicits.systemClock.\\E$"
  }
]
*/
package fix

object DisableInStaticInitImplicits {
  case class Clock(millis: () => Long)
  case class Zone(id: String)

  given systemClock: Clock = Clock(() => System.currentTimeMillis())
  given utc: Zone = Zone("UTC")

  def now(using c: Clock): Long = c.millis()
  def nowIn(offset: Long)(using c: Clock): Long = c.millis() + offset
  def zoneId(using z: Zone): String = z.id

  val a = now/* assert: DisableInStaticInit.clock
          ^^^
  ^\Qfix/DisableInStaticInitImplicits.systemClock.\E$ is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  lazy val b = nowIn(1L)/* assert: DisableInStaticInit.clock
               ^^^^^^^^^
  ^\Qfix/DisableInStaticInitImplicits.systemClock.\E$ is passed implicitly here, which is evaluated once, when its enclosing object is initialized */

  val c = zoneId
  val d = now(using Clock(() => 0L))
  def e = now
  val f = () => nowIn(2L)

  class C {
    val g = now
  }
}
