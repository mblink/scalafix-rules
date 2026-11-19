/*
rule = LinearSizeCompare
*/
package fix

import cats.syntax.eq._
import cats.instances.int._

object LinearSizeCompare {
  def comparisons(xs: List[Int]): List[Boolean] = List(
    xs.size == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    xs.length != 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    xs.size > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    xs.length >= 1,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    xs.size < 1,/* assert: LinearSizeCompare
    ^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    xs.length <= 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    0 == xs.size,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    0 < xs.length,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    1 > xs.size,/* assert: LinearSizeCompare
    ^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    1 <= xs.size,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    xs.size === 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    xs.length =!= 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    xs.size.==(0),/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
  )

  def linear(
      xs: collection.LinearSeq[Int],
      iter: Iterator[Int],
      queue: collection.immutable.Queue[Int],
      generic: Seq[Int],
      iterable: Iterable[Int],
  ): List[Boolean] = List(
    xs.length == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    xs.size > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    iter.size > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    iter.length == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    queue.length == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    queue.size > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    generic.size == 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */
    generic.length > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
    iterable.size > 0,/* assert: LinearSizeCompare
    ^^^^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
  )

  private def list(): List[Int] = List(1)
  private val named = List(1)
  val methodResult = list().size > 0/* assert: LinearSizeCompare
                     ^^^^^^^^^^^^^^^
  Use `nonEmpty` because computing this collection's size can be O(n) */
  val namedReceiver = named.length == 0/* assert: LinearSizeCompare
                      ^^^^^^^^^^^^^^^^^
  Use `isEmpty` because computing this collection's size can be O(n) */

  def other(
      vector: Vector[Int],
      indexed: IndexedSeq[Int],
      array: Array[Int],
      arraySeq: collection.mutable.ArraySeq[Int],
      hashMap: collection.immutable.HashMap[Int, Int],
      hashSet: collection.immutable.HashSet[Int],
      map: Map[Int, Int],
      set: Set[Int],
      mutableMap: collection.mutable.HashMap[Int, Int],
      mutableSet: collection.mutable.HashSet[Int],
      string: String,
      javaList: java.util.ArrayList[Int],
      file: java.io.File,
      xs: List[Int],
      n: Int,
      vectors: List[Vector[Int]],
      lists: Vector[List[Int]],
  ): List[Boolean] = List(
    vectors(0).size == 0,
    vectors.apply(0).length > 0,
    lists(0).size == 0,
    vector.size == 0,
    vector.length > 0,
    indexed.length == 0,
    array.size == 0,
    array.length > 0,
    arraySeq.size == 0,
    arraySeq.length > 0,
    hashMap.size == 0,
    hashSet.size == 0,
    map.size == 0,
    set.size == 0,
    mutableMap.size == 0,
    mutableSet.size == 0,
    string.length > 0,
    javaList.size() == 0,
    javaList.size == 0,
    file.length() == 0,
    file.length == 0,
    n - xs.length > 0,
    xs.size == 1,
    xs.length > 1,
    xs.size >= 0,
  )

  val text = "xs.length > 0" // xs.length > 0
}
