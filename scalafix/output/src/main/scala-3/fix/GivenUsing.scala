package fix

object GivenUsing {
  implicit val i: Int = 1
  implicit val j: Int = 1 // scalafix:ok GivenUsing

  implicit def s: String = ""

  implicit def conversion(i: Int): String = i.toString

  def implicitArg(implicit i: Int): Int = i
}
