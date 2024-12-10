package fix

object StrictSubclassAccess {
  trait Super {
    private[StrictSubclassAccess] val testPrivateWithinExplicitOverrideVal: Unit = ()
    private[StrictSubclassAccess] def testPrivateWithinExplicitOverrideDef(): Unit = ()
  }
  trait Sub1 extends Super {
    override private[fix] val testPrivateWithinExplicitOverrideVal: Unit = println("Sub1.testPrivateWithinExplicitOverrideVal")
    override private[StrictSubclassAccess] def testPrivateWithinExplicitOverrideDef(): Unit = println("Sub1.testPrivateWithinExplicitOverrideDef")
  }
  trait Sub2 extends Sub1 {
    override protected[fix] val testPrivateWithinExplicitOverrideVal: Unit = println("Sub2.testPrivateWithinExplicitOverrideVal")
    override def testPrivateWithinExplicitOverrideDef(): Unit = println("Sub2.testPrivateWithinExplicitOverrideDef")
  }


  sealed trait Adt {
    protected val testProtectedNoExplicitOverrideVal: Int
    protected def testProtectedNoExplicitOverrideDef(i: Int): Int
  }
  case object Member extends Adt {
    val testProtectedNoExplicitOverrideVal = 1
    def testProtectedNoExplicitOverrideDef(i: Int): Int = i

    val nonOverriddenValIsOkay = 3
    def nonOverriddenDefIsOkay(i: Int): Int = i
  }
}
