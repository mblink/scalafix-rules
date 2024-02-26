package fix

import scalafix.v1._

import scala.meta._
import scala.meta.tokens.Token.{KwWith, KwExtends}

case class WithForExtends(pos: Position) extends Diagnostic {
  override def message = "The `with` keyword is unnecessary here, replace with a comma"
  override def position: _root_.scala.meta.Position = pos
}

class NoWithForExtends extends SyntacticRule("NoWithForExtends") {
  @annotation.tailrec
  private def containsSublist(list: List[Token]): Option[Token] = list match {
    case Nil => None
    case (_: KwExtends) :: _ :: _ :: _ :: (wth: KwWith) :: _ => Some(wth)
    case _  :: tail => containsSublist(tail)
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    containsSublist(doc.tokens.toList).map(tok => Patch.lint(WithForExtends(tok.pos))).getOrElse(Patch.empty)
  }
}