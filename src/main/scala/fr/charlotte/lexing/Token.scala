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
    case ShowKeyword
    case UsingKeyword
    case True
    case False
    case EOF
    case Null
    case AllowKeyword

  def isAnIntegerKeyword(t: Type): Boolean =
    t match
      case AllowKeyword => true
      case ShowKeyword => true
      case _ => false

  def isAStringKeyword(t: Type): Boolean =
    t match
      case UsingKeyword => true
      case _ => false

  def isAKeyword(t: Type): Boolean = isAStringKeyword(t) || isAnIntegerKeyword(t)

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
      case "allow" => AllowKeyword
      case "show" => ShowKeyword
      case "<<" => ShowKeyword
      case "limit" => ShowKeyword
      case "using" => UsingKeyword
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
      case ShowKeyword => "show"
      case False => "false"
      case UsingKeyword => "using"
      case AllowKeyword => "allow"
      case Identifier => ""