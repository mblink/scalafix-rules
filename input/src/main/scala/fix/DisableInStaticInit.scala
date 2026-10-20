/*
rule = DisableInStaticInit
DisableInStaticInit.symbols = [
  {
    id = "now"
    pattern = "^\\Qfix/DisableInStaticInit.now().\\E$"
    message = "Don't capture `now` in a static initializer"
  }
  {
    pattern = "^\\Qfix/DisableInStaticInit.today().\\E$"
  }
  {
    id = "cached"
    pattern = "^\\Qfix/DisableInStaticInit.cached.\\E$"
  }
  {
    id = "clock"
    pattern = "^\\Qfix/DisableInStaticInit.Clock.\\E.*$"
  }
]
*/
package fix

object DisableInStaticInit {
  def now(): Long = System.currentTimeMillis()
  def today: Long = now() / 86400000L

  object Clock {
    def tick(): Long = now()
  }

  val a = now()/* assert: DisableInStaticInit.now
          ^^^
  Don't capture `now` in a static initializer */

  lazy val b = today + 1/* assert: DisableInStaticInit
               ^^^^^
  fix/DisableInStaticInit.today(). is evaluated once, when its enclosing object is initialized */

  val c = {
    val local = today/* assert: DisableInStaticInit
                ^^^^^
    fix/DisableInStaticInit.today(). is evaluated once, when its enclosing object is initialized */
    local
  }

  object Nested {
    val d = List(now())/* assert: DisableInStaticInit.now
                 ^^^
    Don't capture `now` in a static initializer */
  }

  val cached = 1
  val usesCached = cached + 1/* assert: DisableInStaticInit.cached
                   ^^^^^^
  fix/DisableInStaticInit.cached. is evaluated once, when its enclosing object is initialized */

  val ticked = Clock.tick()/* assert: DisableInStaticInit.clock
                     ^^^^
  fix/DisableInStaticInit.Clock.tick(). is evaluated once, when its enclosing object is initialized */

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
