/*
rule = StrictSubclassAccess
*/
package fix

object StrictSubclassAccessSignificantIndentation:
  trait Super:
    private[StrictSubclassAccessSignificantIndentation] val testPrivateWithinVal: Unit = ()
    private[StrictSubclassAccessSignificantIndentation] def testPrivateWithinDef(): Unit = ()
  trait Sub1 extends Super:
    override private[fix] val testPrivateWithinVal: Unit = println("Sub1.testPrivateWithinVal")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'private[fix]' does not match superclass access 'private[StrictSubclassAccessSignificantIndentation]' */
    override private[StrictSubclassAccessSignificantIndentation] def testPrivateWithinDef(): Unit = println("Sub1.testPrivateWithinDef")
  trait Sub2 extends Sub1:
    override protected[fix] val testPrivateWithinVal: Unit = println("Sub2.testPrivateWithinVal")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'protected[fix]' does not match superclass access 'private[fix]' */
    override def testPrivateWithinDef(): Unit = println("Sub2.testPrivateWithinDef")/* assert: StrictSubclassAccess
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    Subclass access 'public' does not match superclass access 'private[StrictSubclassAccessSignificantIndentation]' */
