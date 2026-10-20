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
  ^\Qfix/DisableInStaticInitGiven.now().\E$ is evaluated once, when its enclosing object is initialized */

  given stampFrom(using s: String): Stamp = Stamp(now() + s.length)

  val ctx: String ?=> Long = now()

  enum E {
    case A
    def stamp: Long = now()
  }
}

val topLevel = DisableInStaticInitGiven.now()/* assert: DisableInStaticInit
                                        ^^^
^\Qfix/DisableInStaticInitGiven.now().\E$ is evaluated once, when its enclosing object is initialized */
