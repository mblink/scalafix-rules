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

  val a = now

  lazy val b = nowIn(1L)

  val ticked = tick

  val taggedNow = tagOf[Int]

  val pulsed = pulse

  val c = zoneId
  val d = now(using Clock(() => 0L))
  def e = now
  val f = () => nowIn(2L)

  class C {
    val g = now
  }
}
