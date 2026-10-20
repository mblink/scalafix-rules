package fix

import java.util.regex.Pattern
import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scala.annotation.tailrec
import scalafix.config.CustomMessage
import scalafix.internal.config.ScalafixMetaconfigReaders._
import scalafix.v1._
import scala.meta._

case class DisableInStaticInitConfig(symbols: List[CustomMessage[Pattern]], implicits: List[CustomMessage[Pattern]])

object DisableInStaticInitConfig {
  lazy val default: DisableInStaticInitConfig = DisableInStaticInitConfig(Nil, Nil)

  implicit val surface: Surface[DisableInStaticInitConfig] = deriveSurface
  implicit val decoder: ConfDecoder[DisableInStaticInitConfig] = deriveDecoder(default)
}

case class DisableInStaticInitLint(position: Position, symbol: CustomMessage[Pattern], implicitArg: Boolean)
    extends Diagnostic {
  final val message = symbol.message.getOrElse(
    if (implicitArg) s"${symbol.value} is passed implicitly here, which is evaluated once, when its enclosing object is initialized"
    else s"${symbol.value} is evaluated once, when its enclosing object is initialized"
  )
  override final val categoryID = symbol.id.getOrElse(super.categoryID)
}

class DisableInStaticInit(config: DisableInStaticInitConfig) extends SemanticRule("DisableInStaticInit") {
  def this() = this(DisableInStaticInitConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("DisableInStaticInit")(this.config).map(new DisableInStaticInit(_))

  private def isContextFunction(tpe: Type): Boolean =
    tpe match {
      case _: Type.ContextFunction => true
      case _ => false
    }

  @tailrec private def evaluatedStatically(t: Tree): Boolean =
    t.parent match {
      case None => true
      case Some(_: Defn.Def | _: Defn.Macro | _: Defn.Class | _: Defn.Trait | _: Defn.Enum | _: Term.Function |
          _: Term.ContextFunction | _: Term.PolyFunction | _: Term.PartialFunction | _: Term.AnonymousFunction) =>
        false
      case Some(v: Defn.Val) if v.decltpe.exists(isContextFunction) => false
      case Some(g: Defn.GivenAlias) if g.paramClauseGroups.nonEmpty => false
      case Some(g: Defn.Given) if g.paramClauseGroups.nonEmpty => false
      case Some(p) => evaluatedStatically(p)
    }

  private def implicitArgSymbols(tree: SemanticTree): List[Symbol] =
    tree match {
      case ApplyTree(fn, args) => implicitArgSymbols(fn) ++ args.flatMap(argSymbols)
      case _ => Nil
    }

  private def argSymbols(tree: SemanticTree): List[Symbol] =
    tree match {
      case IdTree(info) => List(info.symbol)
      case ApplyTree(fn, args) => argSymbols(fn) ++ args.flatMap(argSymbols)
      case _ => Nil
    }

  private def matching(patterns: List[CustomMessage[Pattern]], sym: Symbol): List[CustomMessage[Pattern]] =
    patterns.filter(_.value.matcher(sym.toString).matches)

  override def fix(implicit doc: SemanticDocument): Patch = {
    val static = doc.tree.collect { case t: Term if evaluatedStatically(t) => t }
    val symbolLints = static.collect { case t: Term.Name =>
      matching(config.symbols, t.symbol).map(DisableInStaticInitLint(t.pos, _, implicitArg = false))
    }.flatten
    val implicitLints = config.implicits match {
      case Nil => Nil
      case patterns =>
        static
          .flatMap(t => t.synthetics.flatMap(implicitArgSymbols).flatMap(matching(patterns, _)).map((t.pos, _)))
          .distinct
          .map { case (pos, p) => DisableInStaticInitLint(pos, p, implicitArg = true) }
    }
    (symbolLints ++ implicitLints).map(Patch.lint).asPatch
  }
}
