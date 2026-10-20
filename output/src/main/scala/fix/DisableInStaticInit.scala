package fix

object DisableInStaticInit {
  def now(): Long = System.currentTimeMillis()
  def today: Long = now() / 86400000L

  val a = now()

  lazy val b = today + 1

  val c = {
    val local = today
    local
  }

  object Nested {
    val d = List(now())
  }

  val e = new Runnable { def run(): Unit = println(now()) }

  def f = now()
  def g(x: Long = today): Long = x
  val h = () => now()
  val i: Int => Long = _ => today
  val j: PartialFunction[Int, Long] = { case _ => now() }

  class C {
    val l = now()
  }

  trait T {
    val m = today
  }
}
