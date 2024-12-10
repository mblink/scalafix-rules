package fix

import metaconfig.{ConfDecoder, Configured}
import metaconfig.generic.{deriveDecoder, deriveSurface, Surface}
import scalafix.v1._
import scala.io.AnsiColor
import scala.meta._

case class NoUnnecessaryPureConfig(color: Boolean)
object NoUnnecessaryPureConfig {
  val default = NoUnnecessaryPureConfig(true)

  implicit val surface: Surface[NoUnnecessaryPureConfig] = deriveSurface
  implicit val decoder: ConfDecoder[NoUnnecessaryPureConfig] = deriveDecoder(default)
}

case class UnnecessaryPureLint(position: Position, color: Boolean) extends Diagnostic {
  private def withColor(c: String): String => String = s => if (color) c ++ s ++ AnsiColor.RESET else s
  private val magenta = withColor(AnsiColor.MAGENTA)
  private val red = withColor(AnsiColor.RED)

  override def message = s"""|
    |The first statement of a ${magenta("for")} comprehension no longer needs to use `<-` along with `.pure`, it can use `=`
    |
    |Before:
    |
    |  ${magenta("for")} {
    |    ${red("x")} <- someStatementHere.pure[SomeType]
    |    ...
    |  } ${magenta("yield")} result
    |
    |After:
    |
    |  ${magenta("for")} {
    |    ${red("x")} = someStatementHere
    |    ...
    |  } ${magenta("yield")} result
    |""".stripMargin
}

private object PureGenerator {
  def unapply(e: Enumerator): Option[Term.ApplyType] =
    e match {
      case Enumerator.Generator(_, t @ Term.ApplyType.After_4_6_0(Term.Select(_, Term.Name("pure")), Type.ArgClause(List(_)))) =>
        Some(t)
      case _ =>
        None
    }
}

class NoUnnecessaryPure(config: NoUnnecessaryPureConfig)
extends SyntacticRule("NoUnnecessaryPure") {
  def this() = this(NoUnnecessaryPureConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("NoUnnecessaryPure")(this.config).map(new NoUnnecessaryPure(_))

  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Term.ForYield.After_4_9_9(Term.EnumeratorsBlock(PureGenerator(t) :: _), _) =>
        Patch.lint(UnnecessaryPureLint(t.pos, config.color))
    }.asPatch
}
