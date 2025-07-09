/*
rule = MapGetOrElse
*/
package fix

object MapGetOrElse {
  val i = Option(1).map(_ + 1).getOrElse(2)/* assert: MapGetOrElse
          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  Use `fold` instead of `map...getOrElse` */

  val s = Right[Int, String]("foo").map(_ ++ "bar").getOrElse("baz")/* assert: MapGetOrElse
          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  Use `fold` instead of `map...getOrElse` */
}
