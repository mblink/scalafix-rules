package fix

object NoUnnecessaryForComprehension {
  val x = for {
    i <- Option(1)
  } yield i

  val y = for {
    i <- Option(1)
    j <- Option(2)
  } yield i + j
}
