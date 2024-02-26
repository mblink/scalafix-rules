package fix

object NoWithForExtends {
  trait SuperA {}

  trait SuperB {}

  trait ChildA extends SuperA with SuperB {}

  trait ChildB extends SuperA, SuperB {}
}