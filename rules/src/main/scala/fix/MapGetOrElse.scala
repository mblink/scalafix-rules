package fix

import scalafix.v1._
import scala.meta._

case class UseFold(position: Position) extends Diagnostic {
  override lazy val message = "Use `fold` instead of `map...getOrElse`"
}

class MapGetOrElse extends SyntacticRule("MapGetOrElse") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
        Term.Select(
          Term.Apply.After_4_6_0(Term.Select(_, Term.Name("map")), _),
          Term.Name("getOrElse")
        ),
        _
      ) =>
        Patch.lint(UseFold(t.pos))
    }.asPatch
}
