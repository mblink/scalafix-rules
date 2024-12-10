/*
rule = GivenUsing
*/
package fix

object GivenUsing {
  implicit val i: Int = 1/* assert: GivenUsing
  ^^^^^^^^
  Use `given` instead of `implicit val` */
  implicit val j: Int = 1 // scalafix:ok GivenUsing

  implicit def s: String = ""/* assert: GivenUsing
  ^^^^^^^^
  Use `given` instead of `implicit def` */

  implicit def conversion(i: Int): String = i.toString

  def implicitArg(implicit i: Int): Int = i/* assert: GivenUsing
                  ^^^^^^^^
  Use `using` instead of `implicit` */
}
