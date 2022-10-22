package fr.charlotte
case class Token(
                tpe: Token.Type,
                text: String,
                startPos: Int
                ):
  override def toString: String = s"[type=$tpe;text=$text;startPos=$startPos]"

object Token:
  enum Type:
    case Identifier
    case OrOperator
    case AndOperator
    case NotOperator
    case LPar
    case RPar
    case True
    case False
    case EOF
