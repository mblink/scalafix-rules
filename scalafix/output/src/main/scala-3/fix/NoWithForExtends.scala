package fix

object NoWithForExtends {
  trait SuperA {}

  trait SuperB {}

  trait SuperC {}

  trait ChildA extends SuperA with SuperB {}

  trait ChildB extends SuperA, SuperB {}

  trait ChildC extends SuperA with SuperB with SuperC {}

  trait ChildD extends SuperA, SuperB, SuperC {}

  trait SuperWithParamA(a: Int) {
    val init = a
  }

  case class ChildE(a: Int) extends SuperWithParamA(a)
    // with comment
    with SuperB {}

  case class ChildF(a: Int) extends SuperWithParamA(a),
  // with comment
  SuperB {}

  object Outer {
    trait ChildG extends SuperA with SuperB {}

    trait ChildH extends SuperA, SuperB {}

    object Inner {
      trait Core {}
    }

    trait Mantle
  }

  trait WithParam[A] {}

  trait WithParamChildA extends WithParam[SuperA with SuperB] with SuperC {}

  trait WithParamChildB extends WithParam[SuperA with SuperB], SuperC {}

  trait DotAccessWith extends Outer.Inner.Core with Outer.Mantle {}

  trait DotAccessComma extends Outer.Inner.Core, Outer.Mantle {}
}