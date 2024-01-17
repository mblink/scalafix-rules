package fix

object NoUnnecessaryCase {
  val a = Some(1).map { case i => i }
  val b = Some(1).map { i => i }

  val c: PartialFunction[Int, Int] = { case i => i }
  val d: PartialFunction[(Int, String), Int] = { case (i, _) => i }

  val e: PartialFunction[Int, Int] = { case i if i > 0 => i }

  val f = Some((1, "foo")).map { case (i, s) => (i + 1, s) }

  val g = Option(1) match {
    case Some(i) => i
    case None => 0
  }
}
