package fix

import java.util.regex.Pattern
import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scalafix.config.CustomMessage
import scalafix.internal.config.ScalafixMetaconfigReaders._
import scalafix.v1._
import scala.meta._

case class DisableConfig(symbols: List[CustomMessage[Pattern]])

object DisableConfig {
  lazy val default: DisableConfig = DisableConfig(Nil)

  implicit val surface: Surface[DisableConfig] = deriveSurface
  implicit val decoder: ConfDecoder[DisableConfig] = deriveDecoder(default)
}

case class DisableLint(position: Position, symbol: CustomMessage[Pattern]) extends Diagnostic {
  final val message = symbol.message.getOrElse(s"${symbol.value} is disabled")
  override final val categoryID = symbol.id.getOrElse(super.categoryID)
}

class Disable(config: DisableConfig) extends SemanticRule("Disable") {
  def this() = this(DisableConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("Disable")(this.config).map(new Disable(_))

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case t: Name =>
        config.symbols.filter(_.value.matcher(t.symbol.toString).matches).map(sym => Patch.lint(DisableLint(t.pos, sym)))
    }.flatten.asPatch
}
