package fix

object NoUnnecessaryPure {
  implicit class PureOps[A](a: A) {
    def pure[F[_]](implicit ev: Option[A] =:= F[A]): F[A] = ev(Some(a))
  }

  val x = for {
    i <- 1.pure[Option]
    j = 2
  } yield i + j
}
