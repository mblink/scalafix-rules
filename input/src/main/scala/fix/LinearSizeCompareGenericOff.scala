/*
rule = LinearSizeCompare
LinearSizeCompare.reportGenericSeq = false
*/
package fix

object LinearSizeCompareGenericOff {
  def comparisons(list: List[Int], seq: Seq[Int], iterable: Iterable[Int]): List[Boolean] = List(
    list.size == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    seq.size == 0,
    iterable.size > 0,
  )
}
