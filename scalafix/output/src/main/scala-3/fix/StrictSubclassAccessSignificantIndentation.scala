package fix

object StrictSubclassAccessSignificantIndentation:
  trait Super:
    private[StrictSubclassAccessSignificantIndentation] val testPrivateWithinVal: Unit = ()
    private[StrictSubclassAccessSignificantIndentation] def testPrivateWithinDef(): Unit = ()
  trait Sub1 extends Super:
    override private[fix] val testPrivateWithinVal: Unit = println("Sub1.testPrivateWithinVal")
    override private[StrictSubclassAccessSignificantIndentation] def testPrivateWithinDef(): Unit = println("Sub1.testPrivateWithinDef")
  trait Sub2 extends Sub1:
    override protected[fix] val testPrivateWithinVal: Unit = println("Sub2.testPrivateWithinVal")
    override def testPrivateWithinDef(): Unit = println("Sub2.testPrivateWithinDef")
