package fix

import scalafix.v1._

import scala.meta._
import scala.reflect.ClassTag
import scala.meta.tokens.Token.{KwExtends, KwWith, LeftBracket, LeftParen, RightBracket, RightParen}

case class WithForExtends(position: Position) extends Diagnostic {
  override def message = "The `with` keyword is unnecessary here, replace with a comma"
}

class NoWithForExtends extends SyntacticRule("NoWithForExtends") {

  private def cleanPairedTokens[L <: Token : ClassTag, R <: Token : ClassTag](tlist: List[Token]): List[Token] = {
    @annotation.tailrec
    def recurseWithFlag(tlist: List[Token], acc: List[Token], flag: Boolean): List[Token] = {
      tlist match {
        case Nil => acc
        case (_: L) :: tail => recurseWithFlag(tail, acc, false)
        case (_ : R) :: tail => recurseWithFlag(tail, acc, true)
        case tok :: tail if flag => recurseWithFlag(tail, acc :+ tok, flag)
        case _ :: tail => recurseWithFlag(tail, acc, flag)
      }
    }

    recurseWithFlag(tlist, List(), true)
  }

  @annotation.tailrec
  private def containsSublist(list: List[Token]): Option[Token] = list match {
    case Nil => None
    case (_: KwExtends) :: _ :: _ :: _ :: (wth: KwWith) :: _ => Some(wth)
    case _  :: tail => containsSublist(tail)
  }

  private def traverseTree(tree: Tree): List[Tokens] = {
    @annotation.tailrec
    def recTreeAcc(tlist: List[Tree], acc: List[Tokens]): List[Tokens] = {
      tlist match {
        case Nil => acc
        case head :: tail =>
          val toks = head.children.collect {
            case t: Template if t.inits.length > 1 => t.tokens
          }
          head.children match {
            case Nil => recTreeAcc(tail, acc ++ toks)
            case _ => recTreeAcc(head.children ++ tail, acc ++ toks)
          }
      }
    }

    recTreeAcc(tree.children, List())
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    println(traverseTree(doc.tree).flatMap(_.toList).map(tok => tok.text)): Unit
    traverseTree(doc.tree)
      .map(_.toList)
      .map(cleanPairedTokens[LeftParen, RightParen])
      .map(cleanPairedTokens[LeftBracket, RightBracket])
      .map(containsSublist).collect {
      case Some(tok) => Patch.lint(WithForExtends(tok.pos))
    }.asPatch
  }
}