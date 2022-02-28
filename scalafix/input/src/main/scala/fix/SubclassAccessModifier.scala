/*
rule = SubclassAccessModifier
*/
package fix

object SubclassAccessModifier {
  trait Super {
    private[SubclassAccessModifier] val testPrivateWithinVal: Unit = ()
    private[SubclassAccessModifier] def testPrivateWithinDef(): Unit = ()
  }
  trait Sub1 extends Super {
    override private[fix] val testPrivateWithinVal: Unit = println("Sub1.testPrivateWithinVal")/* assert: SubclassAccessModifier
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'private[fix]' does not match superclass access 'private[SubclassAccessModifier]' */
    override private[SubclassAccessModifier] def testPrivateWithinDef(): Unit = println("Sub1.testPrivateWithinDef")
  }
  trait Sub2 extends Sub1 {
    override protected[fix] val testPrivateWithinVal: Unit = println("Sub2.testPrivateWithinVal")/* assert: SubclassAccessModifier
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'protected[fix]' does not match superclass access 'private[fix]' */
    override def testPrivateWithinDef(): Unit = println("Sub2.testPrivateWithinDef")/* assert: SubclassAccessModifier
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'public' does not match superclass access 'private[SubclassAccessModifier]' */
  }
}
