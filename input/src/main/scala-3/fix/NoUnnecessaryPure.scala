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

Statements in a for comprehension do not need to use `<-` along with `.pure`, they can use `=`

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
    k <- 3.pure[Option]/* assert: NoUnnecessaryPure
         ^^^^^^^^^^^^^^

Statements in a for comprehension do not need to use `<-` along with `.pure`, they can use `=`

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
  } yield i + j + k
}
