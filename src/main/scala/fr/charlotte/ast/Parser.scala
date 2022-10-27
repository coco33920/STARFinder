package fr.charlotte.ast

import fr.charlotte.lexing.Token
import Ast.Tree.*

import scala.annotation.tailrec

class Parser(input: List[Token]){
  def parse(): Ast =
    def aux(input: List[Token],acc: Ast,lastToken: Token.Type): (Ast,List[Token]) =
      input match
        //end cases
        case i if i.isEmpty => (acc,List.empty[Token])
        case Token(Token.Type.RPar,_,_)::tail => (acc,tail)
        case Token(Token.Type.EOF,_,_)::_ => (acc,List.empty[Token])

        //recursive
        case Token(Token.Type.LPar,_,_)::tail =>
          val (value,t) = aux(tail,Ast(Null),Token.Type.Null)
          lastToken match
            case Token.Type.NotOperator =>
              val n_value = value.applyNotOperator()
              val ast = acc.injectValue(n_value.tpe)
              aux(t,ast,Token.Type.LPar)
            case _ =>
              val ast = acc.injectValue(value.tpe)
              aux(t, ast, Token.Type.LPar)

        //operators
        case Token(Token.Type.AndOperator,_,_)::tail =>
          val ast = acc.applyOperator(Token.Type.AndOperator)
          aux(tail,ast,Token.Type.AndOperator)

        case Token(Token.Type.OrOperator,_,_)::tail =>
          val ast = acc.applyOperator(Token.Type.OrOperator)
          aux(tail,ast,Token.Type.OrOperator)

        case Token(Token.Type.NotOperator,_,_)::tail =>
          aux(tail,acc,Token.Type.NotOperator)

        //Identifiers
        case Token(Token.Type.Identifier, text, _) :: tail =>
          val value = Leaf[String](Parameter[String](Parameter.Type.Argument, text))
          lastToken match
            case Token.Type.NotOperator =>
              val n_val = Ast(value).applyNotOperator()
              val ast = acc.injectValue(n_val.tpe)
              aux(tail,ast,Token.Type.Identifier)
            case _ =>
              val ast = acc.injectValue(value)
              aux(tail, ast, Token.Type.Identifier)


        case _ => (Ast(Null),List.empty[Token])
    val (end,_) = aux(this.input,Ast(Null),Token.Type.Null)
    end
}
