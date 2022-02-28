package fix

import scalafix.v1._
import scala.meta._
import scala.meta.contrib._

class SubclassAccessModifier extends SemanticRule("SubclassAccessModifier") {
  sealed trait Access { val humanReadable: String }
  object Access {
    case object Public extends Access { val humanReadable = "public" }
    case object Protected extends Access { val humanReadable = "protected" }
    case object ProtectedThis extends Access { val humanReadable = "protected[this]" }
    case class ProtectedWithin(within: Symbol) extends Access { val humanReadable = s"protected[${within.displayName}]" }
    case object Private extends Access { val humanReadable = "private" }
    case object PrivateThis extends Access { val humanReadable = "private[this]" }
    case class PrivateWithin(within: Symbol) extends Access { val humanReadable = s"private[${within.displayName}]" }

    private case class Matcher(matches: SymbolInformation => Boolean, access: SymbolInformation => Access)

    private val matchers: List[Matcher] = List(
      Matcher(_.isPrivateWithin, info => PrivateWithin(info.within.get)),
      Matcher(_.isPrivateThis, _ => PrivateThis),
      Matcher(_.isPrivate, _ => Private),
      Matcher(_.isProtectedWithin, info => ProtectedWithin(info.within.get)),
      Matcher(_.isProtectedThis, _ => ProtectedThis),
      Matcher(_.isProtected, _ => Protected),
      Matcher(_.isPublic, _ => Public)
    )

    def unapply(info: SymbolInformation): Option[Access] =
      matchers.collectFirst { case m if m.matches(info) => m.access(info) }.orElse {
        println(s"Unknown symbol access for symbol '$info'")
        None
      }

    def allowed(superAccess: Access, subAccess: Access): Boolean =
      (superAccess, subAccess) match {
        // Subclass vals/methods may have the same access as their superclass equivalents
        case (Public, Public) => true
        case (Protected, Protected) => true
        case (ProtectedThis, ProtectedThis) => true
        case (Private, Private) => true
        case (PrivateThis, PrivateThis) => true

        // Subclass vals/methods may have weaker access than their superclass equivalents
        // Note: the scala compiler itself disallows this, but we don't care
        case (Public, Protected | ProtectedThis | ProtectedWithin(_) | Private | PrivateThis | PrivateWithin(_)) => true
        case (Protected, Private | PrivateThis) => true
        case (ProtectedThis, Private | PrivateThis) => true
        case (ProtectedWithin(_), Private | PrivateThis) => true

        // TODO - allow these?
        case (ProtectedThis | ProtectedWithin(_), Protected) => true
        case (ProtectedWithin(_), ProtectedThis) => true
        case (PrivateThis | PrivateWithin(_), Private) => true
        case (PrivateWithin(_), PrivateThis) => true

        // Subclass vals/methods may not have stronger access than their superclass equivalents
        case (Protected | ProtectedThis | ProtectedWithin(_), Public) => false
        case (Protected, ProtectedThis | ProtectedWithin(_)) => false
        case (Protected, PrivateWithin(_)) => false
        case (ProtectedThis, ProtectedWithin(_)) => false
        case (ProtectedThis, PrivateWithin(_)) => false
        case (ProtectedWithin(_), PrivateWithin(_)) => false
        case (Private | PrivateThis | PrivateWithin(_), Public) => false
        case (Private | PrivateThis | PrivateWithin(_), Protected | ProtectedThis | ProtectedWithin(_)) => false
        case (Private, PrivateThis | PrivateWithin(_)) => false
        case (PrivateThis, PrivateWithin(_)) => false

        // Subclass vals/methods may not have different protected/private within declarations
        case (ProtectedWithin(superWithin), ProtectedWithin(subWithin)) => superWithin == subWithin
        case (PrivateWithin(superWithin), PrivateWithin(subWithin)) => superWithin == subWithin

      }
  }

  case class SubclassAccessMismatch(position: Position, superAccess: Access, subAccess: Access) extends Diagnostic {
    override def message = s"Subclass access '${subAccess.humanReadable}' does not match superclass access '${superAccess.humanReadable}'"
  }

  private def lintSubclassAccess(tree: Tree)(implicit doc: SemanticDocument): Patch =
    tree.symbol.info.flatMap(i => i.overriddenSymbols.headOption.flatMap(_.info.map((_, i)))) match {
      case Some((Access(superAccess), Access(subAccess))) if !Access.allowed(superAccess, subAccess) =>
        Patch.lint(SubclassAccessMismatch(tree.pos, superAccess, subAccess))

      case _ =>
        Patch.empty
    }

  // We only look at the first overridden symbol, i.e. the direct parent of the subclass
  // A mismatch between a superclass and a super-superclass will be attached to the superclass
  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case d: Defn.Def if d.hasMod(Mod.Override()) => lintSubclassAccess(d)
      case v: Defn.Val if v.hasMod(Mod.Override()) => lintSubclassAccess(v)

    }.asPatch
}
