package fix

object LinearSizeCompareGenericOff {
  def comparisons(list: List[Int], seq: Seq[Int], iterable: Iterable[Int]): List[Boolean] = List(
    list.size == 0,
    seq.size == 0,
    iterable.size > 0,
  )
}
