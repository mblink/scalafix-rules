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

  val a = now

  val ticked = tick

  val pulsed = pulse

  val b = now(Clock(() => 0L))
  def c = now
  val d = () => now
}
