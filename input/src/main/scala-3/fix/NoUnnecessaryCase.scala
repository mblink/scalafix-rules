/*
rule = NoUnnecessaryCase
*/
package fix

object NoUnnecessaryCase {
  val a = Some((1, 2)).map { case (i, j) => i + j }/* assert: NoUnnecessaryCase
                             ^^^^^^^^^^^^^^^^^^^^
  The `case` keyword is unnecessary here */

  val b = Some((1, 2)).map((i, j) => i + j)

  val c = Some((1, 2)).map((i, _) => i)

  val d = Some((1, (2, 3))).map { case (i, (j, k)) => i + j + k }
}
