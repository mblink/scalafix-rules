package fix

import scalafix.v1._
import scala.meta._

case class UseGiven(kw: String, position: Position) extends Diagnostic {
  override lazy val message = s"Use `given` instead of `implicit $kw`"
}

case class UseUsing(position: Position) extends Diagnostic {
  override lazy val message = "Use `using` instead of `implicit`"
}

private case class ParsedParams(allImplicitGroups: Boolean, implicitMods: List[Mod])

class GivenUsing extends SyntacticRule("GivenUsing") {
  private def implicitMod(mods: List[Mod]): Option[Mod] = mods.find(_.is[Mod.Implicit])

  private def parseParams(paramGroups: List[Member.ParamClauseGroup]): ParsedParams =
    paramGroups.foldRight(ParsedParams(true, Nil))((group, acc) =>
      group.paramClauses.foldRight(acc) {
        case (clause, ParsedParams(accAll, accImplicitMods)) =>
          implicitMod(clause.mod.toList).map(m => ParsedParams(accAll, m :: accImplicitMods))
            .orElse(clause.mod.find(_.is[Mod.Using]).map(_ => ParsedParams(accAll, accImplicitMods)))
            .getOrElse(ParsedParams(false, accImplicitMods))
      }
    )

  override def fix(implicit doc: SyntacticDocument): Patch =
    doc.tree.collect {
      case Defn.Val(mods, _, _, _) => implicitMod(mods).fold(Patch.empty)(m => Patch.lint(UseGiven("val", m.pos)))

      case Defn.Def.After_4_7_3(mods, _, paramGroups, _, _) =>
        val parsed = parseParams(paramGroups)
        val givenPatch = implicitMod(mods)
          .filter(_ => parsed.allImplicitGroups)
          .fold(Patch.empty)(m => Patch.lint(UseGiven("def", m.pos)))
        val usingPatch = parsed.implicitMods.map(m => Patch.lint(UseUsing(m.pos))).asPatch
        givenPatch + usingPatch
    }.asPatch
}
