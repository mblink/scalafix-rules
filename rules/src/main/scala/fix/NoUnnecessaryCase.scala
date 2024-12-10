package fix

import scalafix.v1._
import scala.meta._

case class UnnecessaryCaseLint(position: Position) extends Diagnostic {
  override def message = "The `case` keyword is unnecessary here"
}

private object SimpleFunctionPat {
  def unapply(pat: Pat): Boolean =
    pat match {
      case Pat.Var(_) | Pat.Wildcard() | Pat.Typed(SimpleFunctionPat(), _) => true
      case _ => false
    }
}

private object SimpleFunctionPats {
  def unapply(pats: List[Pat]): Boolean = pats.forall(SimpleFunctionPat.unapply)
}

class NoUnnecessaryCase extends SyntacticRule("NoUnnecessaryCase") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Term.PartialFunction(List(t @ Case(Pat.Tuple(SimpleFunctionPats()), None, _))) =>
        Patch.lint(UnnecessaryCaseLint(t.pos))
    }.asPatch
}
