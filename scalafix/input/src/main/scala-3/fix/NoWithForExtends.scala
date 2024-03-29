/*
rule = NoWithForExtends
*/
package fix

object NoWithForExtends {
  trait SuperA {}

  trait SuperB {}

  trait SuperC {}

  trait ChildA extends SuperA with SuperB {}/* assert: NoWithForExtends
                              ^^^^
   The `with` keyword is unnecessary here, replace with a comma
   */

  trait ChildB extends SuperA, SuperB {}

  trait ChildC extends SuperA with SuperB with SuperC {}/* assert: NoWithForExtends
                              ^^^^
  The `with` keyword is unnecessary here, replace with a comma
  */

  trait ChildD extends SuperA, SuperB, SuperC {}

  trait SuperWithParamA(a: Int) {
    val init = a
  }

  case class ChildE(a: Int) extends SuperWithParamA(a)
    // with comment
    with SuperB {}/* assert: NoWithForExtends
    ^^^^
     The `with` keyword is unnecessary here, replace with a comma
     */

  case class ChildF(a: Int) extends SuperWithParamA(a),
  // with comment
  SuperB {}

  object Outer {
    trait ChildG extends SuperA with SuperB {}/* assert: NoWithForExtends
                                ^^^^
  The `with` keyword is unnecessary here, replace with a comma
  */

    trait ChildH extends SuperA, SuperB {}

    object Inner {
      trait Core {}
    }

    trait Mantle
  }

  trait WithParam[A] {}

  trait WithParamChildA extends WithParam[SuperA with SuperB] with SuperC {}/* assert: NoWithForExtends
                                                              ^^^^
   The `with` keyword is unnecessary here, replace with a comma
   */

  trait WithParamChildB extends WithParam[SuperA with SuperB], SuperC {}

  trait DotAccessWith extends Outer.Inner.Core with Outer.Mantle {}/* assert: NoWithForExtends
                                               ^^^^
  The `with` keyword is unnecessary here, replace with a comma
  */

  trait DotAccessComma extends Outer.Inner.Core, Outer.Mantle {}
}