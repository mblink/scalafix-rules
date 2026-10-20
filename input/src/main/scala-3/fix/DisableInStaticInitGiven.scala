/*
rule = DisableInStaticInit
DisableInStaticInit.symbols = [
  {
    pattern = "^\\Qfix/DisableInStaticInitGiven.now().\\E$"
  }
]
*/
package fix

object DisableInStaticInitGiven {
  def now(): Long = System.currentTimeMillis()

  case class Stamp(value: Long)

  given Stamp = Stamp(now())/* assert: DisableInStaticInit
                      ^^^
  fix/DisableInStaticInitGiven.now(). is evaluated once, when its enclosing object is initialized */

  given stampFrom(using s: String): Stamp = Stamp(now() + s.length)

  val ctx: String ?=> Long = now()
  val autoEta: () => Long = now/* assert: DisableInStaticInit
                            ^^^
  fix/DisableInStaticInitGiven.now(). is evaluated once, when its enclosing object is initialized */

  enum E {
    case A
    def stamp: Long = now()
  }

  enum Phase(val at: Long) {
    case Boot extends Phase(now())/* assert: DisableInStaticInit
                            ^^^
    fix/DisableInStaticInitGiven.now(). is evaluated once, when its enclosing object is initialized */
  }
}

val topLevel = DisableInStaticInitGiven.now()/* assert: DisableInStaticInit
                                        ^^^
fix/DisableInStaticInitGiven.now(). is evaluated once, when its enclosing object is initialized */
