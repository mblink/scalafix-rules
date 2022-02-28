/*
rule = StrictSubclassAccess
*/
package fix

object StrictSubclassAccess {
  trait Super {
    private[StrictSubclassAccess] val testPrivateWithinExplicitOverrideVal: Unit = ()
    private[StrictSubclassAccess] def testPrivateWithinExplicitOverrideDef(): Unit = ()
  }
  trait Sub1 extends Super {
    override private[fix] val testPrivateWithinExplicitOverrideVal: Unit = println("Sub1.testPrivateWithinExplicitOverrideVal")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'private[fix]' does not match superclass access 'private[StrictSubclassAccess]' */
    override private[StrictSubclassAccess] def testPrivateWithinExplicitOverrideDef(): Unit = println("Sub1.testPrivateWithinExplicitOverrideDef")
  }
  trait Sub2 extends Sub1 {
    override protected[fix] val testPrivateWithinExplicitOverrideVal: Unit = println("Sub2.testPrivateWithinExplicitOverrideVal")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'protected[fix]' does not match superclass access 'private[fix]' */
    override def testPrivateWithinExplicitOverrideDef(): Unit = println("Sub2.testPrivateWithinExplicitOverrideDef")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'public' does not match superclass access 'private[StrictSubclassAccess]' */
  }


  sealed trait Adt {
    protected val testProtectedNoExplicitOverrideVal: Int
    protected def testProtectedNoExplicitOverrideDef(i: Int): Int
  }
  case object Member extends Adt {
    val testProtectedNoExplicitOverrideVal = 1/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'public' does not match superclass access 'protected' */
    def testProtectedNoExplicitOverrideDef(i: Int): Int = i/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'public' does not match superclass access 'protected' */

    val nonOverriddenValIsOkay = 3
    def nonOverriddenDefIsOkay(i: Int): Int = i
  }
}
