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

  case class ChildE(a: Int) extends SuperWithParamA(a) with SuperB {}

  case class ChildF(a: Int) extends SuperWithParamA(a), SuperB {}
}