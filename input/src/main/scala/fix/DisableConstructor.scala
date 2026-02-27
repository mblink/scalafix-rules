/*
rule = DisableConstructor
DisableConstructor.constructors = [
  {
    id = "Ordering"
    pattern = "^\\Qscala/math/Ordering#\\E$"
  }
  {
    id = "BigDecimal"
    pattern = "^scala/(math/|package\\.)BigDecimal(#|\\.)$"
    message = "Don't use BigDecimal"
  }
]
*/
package fix

object DisableConstructor {
  val o = new scala.math.Ordering[Unit] { def compare(x: Unit, y: Unit) = 0 }/* assert: DisableConstructor.Ordering
          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  ^\Qscala/math/Ordering#\E$ is disabled */

  val d1 = BigDecimal(123)/* assert: DisableConstructor.BigDecimal
           ^^^^^^^^^^^^^^^
  Don't use BigDecimal */

  val d2 = new BigDecimal(new java.math.BigDecimal(123))/* assert: DisableConstructor.BigDecimal
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  Don't use BigDecimal */

  val e = d1.equals(d2)
}
