package fix

object DisableInStaticInitGiven {
  def now(): Long = System.currentTimeMillis()

  case class Stamp(value: Long)

  given Stamp = Stamp(now())

  given stampFrom(using s: String): Stamp = Stamp(now() + s.length)

  val ctx: String ?=> Long = now()

  enum E {
    case A
    def stamp: Long = now()
  }
}

val topLevel = DisableInStaticInitGiven.now()
