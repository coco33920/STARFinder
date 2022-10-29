package fr.charlotte.ast

import fr.charlotte.STARException
import fr.charlotte.lexing.Token
import fr.charlotte.ast.Parameter

object Ast:
  enum Tree:
    case Null
    case Leaf[A](s: Parameter[A])
    case Node[A](s: Parameter[A], s1: Tree, s2: Tree)

case class Ast(tpe: Ast.Tree):

  def print(): String = {
    def recursive_print(b: Ast.Tree): String = {
      b match
        case Ast.Tree.Null => ""
        case Ast.Tree.Leaf(s:Parameter[_]) => s.print
        case Ast.Tree.Node(s:Parameter[_],s1: Ast.Tree, s2: Ast.Tree) => {
          val (v1,v2) = (recursive_print(s1),recursive_print(s2))
          s.tpe match
            case Parameter.Type.None | Parameter.Type.Argument => v1+v2
            case Parameter.Type.NotOperator => s.print + "(" + v1 + ")"
            case Parameter.Type.AndOperator | Parameter.Type.OrOperator | Parameter.Type.ToOperator => "(" + v1 + " " + s.print + " " + v2 + ")"
        }
    }
    recursive_print(this.tpe)
  }

  def injectValue(b: Ast.Tree): Ast = {
    this.tpe match
      case Ast.Tree.Null => Ast(b)
      case Ast.Tree.Leaf(s: Parameter[_]) => Ast(Ast.Tree.Node(Parameter(Parameter.Type.Argument, ""), Ast.Tree.Leaf(s), b))
      case Ast.Tree.Node(s: Parameter[_], s1: Ast.Tree, Ast.Tree.Null) => Ast(Ast.Tree.Node(s, s1, b))
      case Ast.Tree.Node(s: Parameter[_], Ast.Tree.Null, s1: Ast.Tree) => Ast(Ast.Tree.Node(s, b, s1))
      case Ast.Tree.Node(s: Parameter[_], s1: Ast.Tree, s2: Ast.Tree) => Ast(Ast.Tree.Node(s, Ast.Tree.Node(s, s1, s2), b))
  }
  
  def injectToAllow(i: Int): Ast = {
    this.tpe match
      case Ast.Tree.Node(s: Parameter[_],s1: Ast.Tree, s2: Ast.Tree) => 
        s.tpe match
          case Parameter.Type.ToOperator => Ast(Ast.Tree.Node(Parameter[Int](Parameter.Type.ToOperator, i),s1,s2))
          case _ => throw new STARException("Syntax Error","Error while parsing, allow should only be used after a to operator")
      case _ => throw new STARException("Syntax Error","Error while parsing, allow should only be used after a to operator")
  }

  def applyNotOperator(): Ast = {
    Ast(
      Ast.Tree.Node(
        Parameter[String](Parameter.Type.NotOperator, ""),
        this.tpe,
        Ast.Tree.Leaf(Parameter[String](Parameter.Type.None, ""))
      )
    )
  }

  def createAst(b: Token.Type): Ast = {
    Ast(
      Ast.Tree.Node(
        Parameter[String](Parameter.fromToken(b), ""),
        Ast.Tree.Null,
        Ast.Tree.Null
      )
    )
  }
    def createAstWithValue(b: Token.Type, s:Parameter[_]): Ast = {
      Ast(
        Ast.Tree.Node(
          Parameter[String](Parameter.fromToken(b), ""),
          Ast.Tree.Leaf(s),
          Ast.Tree.Null
        ))
    }

  def applyOperator(b: Token.Type): Ast = {
    this.tpe match
      case Ast.Tree.Null => createAst(b)

      case Ast.Tree.Leaf(s: Parameter[_]) => createAstWithValue(b,s)

      case Ast.Tree.Node(s: Parameter[_], s1: Ast.Tree, s2: Ast.Tree) =>
        if Parameter.isAnOperator(s.tpe) then Ast(
          Ast.Tree.Node(
            Parameter[String](Parameter.fromToken(b), ""),
            Ast.Tree.Node(s,s1,s2),
            Ast.Tree.Null
          ))
        else Ast(
          Ast.Tree.Node(
            Parameter[String](Parameter.fromToken(b), ""),
            Ast.Tree.Node(Parameter[String](Parameter.fromToken(b), ""), s1, s2),
            Ast.Tree.Null
          ))
  }