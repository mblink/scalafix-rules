/*
rule = NoUnnecessaryForComprehension
NoUnnecessaryForComprehension.color = false
*/
package fix

object NoUnnecessaryForComprehension {
  val x = for {/* assert: NoUnnecessaryForComprehension
          ^

A for comprehension with only one statement can be simplified

Cases where the yield returns the result of the statement can just be the statement itself:

Before:

  for {
    x <- someStatementHere
  } yield x

After:

  someStatementHere

Cases where the yield performs an additional computation can be rewritten with map:

Before:

  for {
    x <- someStatementHere
  } yield doSomethingElse(x)

After:

  someStatementHere.map(doSomethingElse) */
    i <- Option(1)
  } yield i

  val y = for {
    i <- Option(1)
    j <- Option(2)
  } yield i + j
}
