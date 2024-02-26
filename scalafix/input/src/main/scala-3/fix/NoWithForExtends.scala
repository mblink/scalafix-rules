/*
rule = NoWithForExtends
*/
package fix

object NoWithForExtends {
  trait SuperA {}

  trait SuperB {}

  trait ChildA extends SuperA with SuperB {}/* assert: NoWithForExtends
                              ^^^^
   The `with` keyword is unnecessary here, replace with a comma
   */

  trait ChildB extends SuperA, SuperB {}
}