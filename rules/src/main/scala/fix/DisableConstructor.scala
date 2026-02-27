package fix

import java.util.regex.Pattern
import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scalafix.config.CustomMessage
import scalafix.internal.config.ScalafixMetaconfigReaders._
import scalafix.v1._
import scala.meta._

case class DisableConstructorConfig(constructors: List[CustomMessage[Pattern]])
object DisableConstructorConfig {
  val default = DisableConstructorConfig(Nil)

  implicit val surface: Surface[DisableConstructorConfig] = deriveSurface
  implicit val decoder: ConfDecoder[DisableConstructorConfig] = deriveDecoder(default)
}

case class DisableConstructorLint(position: Position, constructor: CustomMessage[Pattern]) extends Diagnostic {
  final val message = constructor.message.getOrElse(s"${constructor.value} is disabled")
  override final val categoryID = constructor.id.getOrElse(super.categoryID)
}

class DisableConstructor(config: DisableConstructorConfig) extends SemanticRule("DisableConstructor") {
  def this() = this(DisableConstructorConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("DisableConstructor")(this.config).map(new DisableConstructor(_))

  private def checkSignature(tree: Tree, pos: Position)(implicit doc: SemanticDocument): List[Patch] = {
    val symStr = tree.symbol.toString
    config.constructors.collect {
      case c if c.value.matcher(symStr).matches => Patch.lint(DisableConstructorLint(pos, c))
    }
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case t @ Term.New(Init.After_4_6_0(tpe, _, _)) => checkSignature(tpe, t.pos)
      case t @ Term.NewAnonymous(Template.After_4_9_9(_, inits, _, _)) => inits.flatMap(checkSignature(_, t.pos))
      case t @ Term.Apply.After_4_6_0(fn, _) => checkSignature(fn, t.pos)
      case _ => Nil
    }.flatten.asPatch
}
