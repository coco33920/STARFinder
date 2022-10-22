package fr.charlotte.ast

//Ast => Null | Node of type * ast * ast

object Parameter:
  enum Type:
    case None
    case Argument
    case OrOperator
    case NotOperator
    case AndOperator

case class Parameter[A](
                         tpe: Parameter.Type,
                         body: A
                       ):
  override def toString: String = s"[Parameter {Type:$tpe} {Body:$body}]"