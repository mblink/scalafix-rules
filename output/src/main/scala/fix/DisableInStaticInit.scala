package fix

object DisableInStaticInit {
  def now(): Long = System.currentTimeMillis()
  def today: Long = now() / 86400000L

  object Clock {
    def tick(): Long = now()
  }

  val a = now()

  lazy val b = today + 1

  val c = {
    val local = today
    local
  }

  object Nested {
    val d = List(now())
  }

  val cached = 1
  val usesCached = cached + 1

  val ticked = Clock.tick()

  val e = new Runnable { def run(): Unit = println(now()) }

  def f = now()
  def g(x: Long = today): Long = x
  val h = () => now()
  val i: Int => Long = _ => today
  val j: PartialFunction[Int, Long] = { case _ => now() }
  val k = now _

  class C {
    val l = now()
  }

  trait T {
    val m = today
  }
}
