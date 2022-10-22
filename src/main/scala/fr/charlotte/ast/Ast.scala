package fr.charlotte.ast

object Ast:
  enum Tree:
    case Null
    case Leaf[A](s:Parameter[A])
    case Node[A](s:Parameter[A],s1:Tree,s2:Tree)

case class Ast(
              tpe:Ast.Tree,
              )