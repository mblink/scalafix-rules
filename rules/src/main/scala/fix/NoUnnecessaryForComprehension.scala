package fix

import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scalafix.v1._
import scala.io.AnsiColor
import scala.meta._

case class NoUnnecessaryForComprehensionConfig(color: Boolean)
object NoUnnecessaryForComprehensionConfig {
  val default = NoUnnecessaryForComprehensionConfig(true)

  implicit val surface: Surface[NoUnnecessaryForComprehensionConfig] = deriveSurface
  implicit val decoder: ConfDecoder[NoUnnecessaryForComprehensionConfig] = deriveDecoder(default)
}

case class UnnecessaryForComprehensionLint(position: Position, color: Boolean) extends Diagnostic {
  private def withColor(c: String): String => String = s => if (color) c ++ s ++ AnsiColor.RESET else s
  private val magenta = withColor(AnsiColor.MAGENTA)
  private val red = withColor(AnsiColor.RED)

  override def message = s"""|
    |A ${magenta("for")} comprehension with only one statement can be simplified
    |
    |Cases where the ${magenta("yield")} returns the result of the statement can just be the statement itself:
    |
    |Before:
    |
    |  ${magenta("for")} {
    |    ${red("x")} <- someStatementHere
    |  } ${magenta("yield")} x
    |
    |After:
    |
    |  someStatementHere
    |
    |Cases where the ${magenta("yield")} performs an additional computation can be rewritten with ${magenta("map")}:
    |
    |Before:
    |
    |  ${magenta("for")} {
    |    ${red("x")} <- someStatementHere
    |  } ${magenta("yield")} doSomethingElse(x)
    |
    |After:
    |
    |  someStatementHere.map(doSomethingElse)
    |""".stripMargin
}

class NoUnnecessaryForComprehension(config: NoUnnecessaryForComprehensionConfig)
extends SyntacticRule("NoUnnecessaryForComprehension") {
  def this() = this(NoUnnecessaryForComprehensionConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("NoUnnecessaryForComprehension")(this.config).map(new NoUnnecessaryForComprehension(_))

  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case t @ Term.ForYield.After_4_9_9(Term.EnumeratorsBlock(List(_)), _) =>
        Patch.lint(UnnecessaryForComprehensionLint(t.pos, config.color))
    }.asPatch
}
