package fix

object DisableInStaticInitGiven {
  def now(): Long = System.currentTimeMillis()

  case class Stamp(value: Long)

  given Stamp = Stamp(now())

  given stampFrom(using s: String): Stamp = Stamp(now() + s.length)

  val ctx: String ?=> Long = now()
  val autoEta: () => Long = now

  enum E {
    case A
    def stamp: Long = now()
  }

  enum Phase(val at: Long) {
    case Boot extends Phase(now())
  }
}

val topLevel = DisableInStaticInitGiven.now()
