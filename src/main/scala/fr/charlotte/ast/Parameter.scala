package fr.charlotte.ast
//Ast => Null | Node of type * ast * ast

import fr.charlotte.lexing.Token

object Parameter:
  enum Type:
    case None
    case Argument
    case OrOperator
    case NotOperator
    case AndOperator
    case ToOperator

  def isAnOperator(t: Parameter.Type): Boolean =
    t match
      case Type.OrOperator | Type.AndOperator | Type.NotOperator | Type.ToOperator => true
      case _ => false

  def fromToken(t: Token.Type): Parameter.Type = {
    t match
      case Token.Type.OrOperator => Type.OrOperator
      case Token.Type.AndOperator => Type.AndOperator
      case Token.Type.NotOperator => Type.NotOperator
      case Token.Type.ToOperator => Type.ToOperator
      case _ => Type.None
  }

case class Parameter[A](
                         tpe: Parameter.Type,
                         body: A
                       ):
  override def toString: String = s"[Parameter {Type:$tpe} {Body:$body}]"

  def print: String =
    this.tpe match
      case Parameter.Type.None => ""
      case Parameter.Type.Argument => this.body.toString
      case Parameter.Type.OrOperator => "∪"
      case Parameter.Type.AndOperator => "∩"
      case Parameter.Type.NotOperator => "¬"
      case Parameter.Type.ToOperator => "→"
