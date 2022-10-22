package fr.charlotte

import fr.charlotte.Token.Type.{AndOperator, False, Identifier, NotOperator, OrOperator, True}

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
  
  def stringToTokenType(text: String): Type =
    text match
      case "or" => OrOperator
      case "and" => AndOperator
      case "not" => NotOperator
      case "true" => True
      case "false" => False
      case _ => Identifier