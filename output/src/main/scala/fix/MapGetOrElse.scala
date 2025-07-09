package fix

object MapGetOrElse {
  val i = Option(1).map(_ + 1).getOrElse(2)

  val s = Right[Int, String]("foo").map(_ ++ "bar").getOrElse("baz")
}
