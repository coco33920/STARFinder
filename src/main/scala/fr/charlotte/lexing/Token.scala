package fr.charlotte.lexing
import fr.charlotte.lexing.Token.Type.*

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
    case ToOperator
    case Quote
    case LPar
    case RPar
    case True
    case False
    case EOF
    case Null

  def stringToTokenType(text: String): Type =
    text.trim match
      case "or" => OrOperator
      case "and" => AndOperator
      case "not" => NotOperator
      case "to" => ToOperator
      case "\"" => Quote
      case "->" => ToOperator
      case "true" => True
      case "false" => False
      case _ => Identifier
  def printToken(t: Token.Type): String =
    t match
      case OrOperator => "or"
      case AndOperator => "and"
      case NotOperator => "not"
      case ToOperator => "to"
      case Quote => "\""
      case True => "true"
      case LPar => "("
      case RPar => ")"
      case Null => ""
      case EOF => "<EOF>"
      case False => "false"
      case Identifier => ""