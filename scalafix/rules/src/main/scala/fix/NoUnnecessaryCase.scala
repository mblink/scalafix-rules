package fix

import scalafix.v1._
import scala.meta._

case class UnnecessaryCase(position: Position) extends Diagnostic {
  override def message = "The `case` keyword is unnecessary here"
}

class NoUnnecessaryCase extends SyntacticRule("NoUnnecessaryCase") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Term.PartialFunction(List(t @ Case(Pat.Var(Term.Name(_)), None, _))) =>
        Patch.lint(UnnecessaryCase(t.pos))
    }.asPatch
}
