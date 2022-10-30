package fr.charlotte.ast

import fr.charlotte.STARException
import fr.charlotte.lexing.Token
import fr.charlotte.ast.Parameter
import fr.charlotte.runtime.Translator

import scala.collection.mutable

object Ast:
  enum Tree:
    case Null
    case Leaf[A](s: Parameter[A])
    case Node[A](s: Parameter[A], s1: Tree, s2: Tree)

case class Ast(tpe: Ast.Tree):

  def printBody(body: Any): String = {
    if(body.toString.equalsIgnoreCase(""))
      return ""
    val s = body.toString.split(";")
    if(s.isEmpty)
      return ""
    val builder = mutable.StringBuilder()
    for(i <- 0 until s.length){
      val v = s(i).split(":")
      if(v.length >= 2){
        builder.append(s"${v(0)} ${v(1)} ")
      }
    }
    builder.toString();
  }

  def print(): String = {
    def recursive_print(b: Ast.Tree): String = {
      b match
        case Ast.Tree.Null => ""
        case Ast.Tree.Leaf(s:Parameter[_]) => s.print
        case Ast.Tree.Node(Parameter(stpe,body),s1: Ast.Tree, s2: Ast.Tree) => {
          val (v1,v2) = (recursive_print(s1),recursive_print(s2))
          stpe match
            case Parameter.Type.None | Parameter.Type.Argument => v1+v2
            case Parameter.Type.NotOperator => (Parameter(stpe,body)).print + "(" + v1 + s") ${printBody(body)}".trim
            case Parameter.Type.AndOperator | Parameter.Type.OrOperator | Parameter.Type.ToOperator => "(" + v1 + " " + (Parameter(stpe,body)).print + " " + v2 + s") ${printBody(body)}".trim
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
          case Parameter.Type.ToOperator => Ast(Ast.Tree.Node(Parameter[String](Parameter.Type.ToOperator, s"allow:$i;"),s1,s2))
          case _ => throw new STARException("Syntax Error","Error while parsing, allow should only be used after a to operator")
      case _ => throw new STARException("Syntax Error","Error while parsing, allow should only be used after a to operator")
  }

  def injectKeyword[T](i: T,b: Token.Type): Ast = {
    if(!Token.isAKeyword(b))
      throw new STARException("Syntax Error", "Error while parsing, you should use a keyword")
    this.tpe match
      case Ast.Tree.Node(Parameter(stpe,body),s1:Ast.Tree,s2:Ast.Tree) =>
        if Parameter.isAnOperator(stpe) then
          var r = ""
          if body.toString.contains(Token.printToken(b)) then {
            val c = Token.printToken(b) + ":"
            val r2 = body.toString.split(";")
            val rc = r2.filter(s => s.contains(c))(0)
            val nb = body.toString.replace(rc, s"${Token.printToken(b)}:$i")
            r = nb
          }else{
            r = body.toString + s"${Token.printToken(b)}:$i;"
          }
          Ast(Ast.Tree.Node(Parameter[String](stpe, r),s1,s2))
        else
          throw new STARException("Syntax Error", s"Error while parsing, you should use ${Token.printToken(b)} after an operator")
      case _ => throw new STARException("Syntax Error",s"Error while parsing, you should use ${Token.printToken(b)} after an operator")
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