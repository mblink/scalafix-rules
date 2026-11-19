package fix

import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scala.annotation.tailrec
import scalafix.v1._
import scala.meta._

case class LinearSizeCompareConfig(reportGenericSeq: Boolean)

object LinearSizeCompareConfig {
  lazy val default: LinearSizeCompareConfig = LinearSizeCompareConfig(reportGenericSeq = true)

  implicit val surface: Surface[LinearSizeCompareConfig] = deriveSurface
  implicit val decoder: ConfDecoder[LinearSizeCompareConfig] = deriveDecoder(default)
}

object LinearSizeCompare {
  private val linearMethods = SymbolMatcher.exact(
    "scala/collection/LinearSeqOptimized#length().",
    "scala/collection/LinearSeqOps#length().",
    "scala/collection/Iterator#length().",
    "scala/collection/immutable/List#length().",
    "scala/collection/immutable/Queue#length().",
    "scala/collection/immutable/Stream#length().",
  )

  private val receiverDependentMethods = SymbolMatcher.exact(
    "scala/collection/SeqLike#length().",
    "scala/collection/SeqLike#size().",
    "scala/collection/SeqOps#length().",
    "scala/collection/SeqOps#size().",
    "scala/collection/TraversableOnce#size().",
    "scala/collection/IterableOnceOps#size().",
  )

  private val linearReceivers = SymbolMatcher.exact(
    "scala/collection/Iterator#",
    "scala/collection/LinearSeq#",
    "scala/collection/View#",
    "scala/collection/immutable/LazyList#",
    "scala/collection/immutable/LinearSeq#",
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Queue#",
    "scala/collection/immutable/Stream#",
    "scala/collection/mutable/LinearSeq#",
    "scala/package.Iterator#",
    "scala/package.LazyList#",
    "scala/package.List#",
    "scala/package.Stream#",
  )

  private val genericReceivers = SymbolMatcher.exact(
    "scala/collection/Iterable#",
    "scala/collection/Seq#",
    "scala/collection/immutable/Iterable#",
    "scala/collection/immutable/Seq#",
    "scala/collection/mutable/Iterable#",
    "scala/collection/mutable/Seq#",
    "scala/package.Iterable#",
    "scala/package.Seq#",
  )

  private sealed abstract class EmptinessCheck(val method: String)
  private case object IsEmpty extends EmptinessCheck("isEmpty")
  private case object NonEmpty extends EmptinessCheck("nonEmpty")

  private case class UseEmptinessCheck(position: Position, check: EmptinessCheck) extends Diagnostic {
    override def message: String = s"Use `${check.method}` because computing this collection's size can be O(n)"
  }
}

class LinearSizeCompare(config: LinearSizeCompareConfig) extends SemanticRule("LinearSizeCompare") {
  import LinearSizeCompare._

  def this() = this(LinearSizeCompareConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("LinearSizeCompare")(this.config).map(new LinearSizeCompare(_))

  @tailrec private def appliedFunction(fun: Term): Term =
    fun match {
      case t: Term.Apply => appliedFunction(t.fun)
      case t: Term.ApplyType => appliedFunction(t.fun)
      case t => t
    }

  private def receiverType(receiver: Term)(implicit doc: SemanticDocument): Option[Symbol] =
    receiver match {
      case _: Term.Apply | _: Term.ApplyType =>
        appliedFunction(receiver).symbol.info.map(_.signature).collect {
          case MethodSignature(_, _, TypeRef(_, symbol, _)) => symbol
        }
      case t =>
        t.symbol.info.map(_.signature).collect {
          case ValueSignature(TypeRef(_, symbol, _)) => symbol
          case MethodSignature(_, _, TypeRef(_, symbol, _)) => symbol
        }
    }

  private def hasLinearSize(receiver: Term, method: Symbol)(implicit doc: SemanticDocument): Boolean =
    linearMethods.matches(method) ||
      (receiverDependentMethods.matches(method) && receiverType(receiver).exists { tpe =>
        linearReceivers.matches(tpe) || (config.reportGenericSeq && genericReceivers.matches(tpe))
      })

  private def replacement(op: Term.Name, bound: Int, sizeOnLeft: Boolean)(implicit doc: SemanticDocument): Option[EmptinessCheck] =
    (op.value, bound, sizeOnLeft) match {
      case ("==", 0, _) | ("<=", 0, true) | (">=", 0, false) | ("<", 1, true) | (">", 1, false) => Some(IsEmpty)
      case ("!=", 0, _) | (">", 0, true) | ("<", 0, false) | (">=", 1, true) | ("<=", 1, false) => Some(NonEmpty)
      case ("===", 0, _) if op.symbol.value == "cats/syntax/EqOps#`===`()." => Some(IsEmpty)
      case ("=!=", 0, _) if op.symbol.value == "cats/syntax/EqOps#`=!=`()." => Some(NonEmpty)
      case _ => None
    }

  private def comparison(left: Term, op: Term.Name, right: Term, position: Position)(implicit doc: SemanticDocument): Option[Patch] = {
    def lint(receiver: Term, size: Term.Name, bound: Int, sizeOnLeft: Boolean): Option[Patch] =
      replacement(op, bound, sizeOnLeft)
        .filter(_ => hasLinearSize(receiver, size.symbol))
        .map(check => Patch.lint(UseEmptinessCheck(position, check)))

    (left, right) match {
      case (Term.Select(receiver, size), Lit.Int(bound)) => lint(receiver, size, bound, sizeOnLeft = true)
      case (Lit.Int(bound), Term.Select(receiver, size)) => lint(receiver, size, bound, sizeOnLeft = false)
      case _ => None
    }
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case t @ Term.ApplyInfix.After_4_6_0(left, op, Type.ArgClause(Nil), Term.ArgClause(List(right), None)) =>
        comparison(left, op, right, t.pos)
      case t @ Term.Apply.After_4_6_0(Term.Select(left, op), Term.ArgClause(List(right), None)) =>
        comparison(left, op, right, t.pos)
    }.flatten.asPatch
}
