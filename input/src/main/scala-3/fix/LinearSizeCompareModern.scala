/*
rule = LinearSizeCompare
*/
package fix

object LinearSizeCompareModern {
  def comparisons(
      lazyList: LazyList[Int],
      view: collection.View[Int],
      arraySeq: collection.immutable.ArraySeq[Int],
  ): List[Boolean] = List(
    lazyList.size == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    lazyList.length != 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    view.size == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    arraySeq.size == 0,
    arraySeq.length > 0,
  )
}
