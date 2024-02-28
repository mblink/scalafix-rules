package fix

import scalafix.v1._

import scala.meta._
import scala.reflect.ClassTag
import scala.meta.tokens.Token.{KwExtends, KwWith, LeftBracket, LeftParen, RightBracket, RightParen, Whitespace, Dot, Comment}

case class WithForExtends(position: Position) extends Diagnostic {
  override def message = "The `with` keyword is unnecessary here, replace with a comma"
}

class NoWithForExtends extends SyntacticRule("NoWithForExtends") {

  private def cleanPairedTokens[L <: Token : ClassTag, R <: Token : ClassTag](tlist: List[Token]): List[Token] = {
    @annotation.tailrec
    def recurseWithFlag(tlist: List[Token], acc: List[Token], flag: Int): List[Token] = {
      tlist match {
        case Nil => acc
        case (_: L) :: tail => recurseWithFlag(tail, acc, flag + 1)
        case (_ : R) :: tail => recurseWithFlag(tail, acc, flag - 1)
        case tok :: tail if flag == 0 => recurseWithFlag(tail, acc :+ tok, flag)
        case _ :: tail => recurseWithFlag(tail, acc, flag)
      }
    }

    recurseWithFlag(tlist, List(), 0)
  }

  private def cleanSingleToken[T <: Token : ClassTag](tlist: List[Token]): List[Token] = {
    @annotation.tailrec
    def recurseOnList(tlist: List[Token], acc: List[Token]): List[Token] = {
      tlist match {
        case Nil => acc
        case (_: T) :: tail => recurseOnList(tail, acc)
        case tok :: tail => recurseOnList(tail, acc :+ tok)
      }
    }

    recurseOnList(tlist, List())
  }

  private def cleanDotAccesses(tlist: List[Token]): List[Token] = {
    @annotation.tailrec
    def recurseOnList(tlist: List[Token], acc: List[Token]): List[Token] = {
      tlist match {
        case Nil => acc
        case (_: Dot) :: _ :: tail => recurseOnList(tail, acc)
        case tok :: tail => recurseOnList(tail, acc :+ tok)
      }
    }
    recurseOnList(tlist, List())
  }

  @annotation.tailrec
  private def containsSublist(list: List[Token]): Option[Token] = list match {
    case Nil => None
    case (_: KwExtends) :: _ :: (wth: KwWith) :: _ => Some(wth)
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
    traverseTree(doc.tree)
      .map(_.toList)
      .map(cleanPairedTokens[LeftParen, RightParen])
      .map(cleanPairedTokens[LeftBracket, RightBracket])
      .map(cleanSingleToken[Comment])
      .map(cleanSingleToken[Whitespace])
      .map(cleanDotAccesses)
      .map(containsSublist).collect {
      case Some(tok) => Patch.lint(WithForExtends(tok.pos))
    }.asPatch
  }
}