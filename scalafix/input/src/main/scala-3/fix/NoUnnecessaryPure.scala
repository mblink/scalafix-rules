/*
rule = NoUnnecessaryPure
NoUnnecessaryPure.color = false
*/
package fix

object NoUnnecessaryPure {
  implicit class PureOps[A](a: A) {
    def pure[F[_]](implicit ev: Option[A] =:= F[A]): F[A] = ev(Some(a))
  }

  val x = for {
    i <- 1.pure[Option]/* assert: NoUnnecessaryPure
         ^^^^^^^^^^^^^^

The first statement of a for comprehension no longer needs to use `<-` along with `.pure`, it can use `=`

Before:

  for {
    x <- someStatementHere.pure[SomeType]
    ...
  } yield result

After:

  for {
    x = someStatementHere
    ...
  } yield result */
    j = 2
  } yield i + j
}
