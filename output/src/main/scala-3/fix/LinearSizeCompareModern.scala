package fix

object LinearSizeCompareModern {
  def comparisons(
      lazyList: LazyList[Int],
      view: collection.View[Int],
      arraySeq: collection.immutable.ArraySeq[Int],
  ): List[Boolean] = List(
    lazyList.size == 0,
    lazyList.length != 0,
    view.size == 0,
    arraySeq.size == 0,
    arraySeq.length > 0,
  )
}
