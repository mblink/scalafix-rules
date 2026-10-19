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
]
*/
package fix

object DisableInStaticInit {
  def now(): Long = System.currentTimeMillis()
  def today: Long = now() / 86400000L

  val a = now()/* assert: DisableInStaticInit.now
          ^^^
  Don't capture `now` in a static initializer */

  lazy val b = today + 1/* assert: DisableInStaticInit
               ^^^^^
  ^\Qfix/DisableInStaticInit.today().\E$ is evaluated once, when its enclosing object is initialized */

  val c = {
    val local = today/* assert: DisableInStaticInit
                ^^^^^
    ^\Qfix/DisableInStaticInit.today().\E$ is evaluated once, when its enclosing object is initialized */
    local
  }

  object Nested {
    val d = List(now())/* assert: DisableInStaticInit.now
                 ^^^
    Don't capture `now` in a static initializer */
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
