package fr.charlotte.ast

import fr.charlotte.lexing.Token
import Ast.Tree.*
import fr.charlotte.STARException
import fr.charlotte.runtime.Translator

import scala.annotation.tailrec

class Parser(input: List[Token]){

  def parse_string(input: List[Token]): (String,List[Token]) = {
    @tailrec
    def aux(input: List[Token], acc: String): (String,List[Token]) = {
      input match
        case Token(Token.Type.Quote, _, _)::tail => (acc.trim,tail)
        case Token(Token.Type.Identifier, text, _)::tail => aux(tail,(acc+" "+text))
        case Token(t,_,_)::tail => aux(tail, acc+" "+Token.printToken(t))
        case _ => (acc,List.empty[Token])
    }
    aux(input, "")
  }

  def parse(): Ast =
    def aux(input: List[Token],acc: Ast,lastToken: Token.Type): (Ast,List[Token]) =
      input match
        //end cases
        case i if i.isEmpty => (acc,List.empty[Token])
        case Token(Token.Type.RPar,_,_)::tail => (acc,tail)
        case Token(Token.Type.EOF,_,_)::_ => (acc,List.empty[Token])
        case Token(Token.Type.AllowKeyword,_,_)::tail => aux(tail,acc,Token.Type.AllowKeyword)
        case Token(Token.Type.ShowKeyword,_,_)::tail => aux(tail,acc,Token.Type.ShowKeyword)
        case Token(Token.Type.Quote,_,_)::tail =>
          val (str,t) = parse_string(tail)
          val a = Ast.Tree.Leaf[String](Parameter(Parameter.Type.Argument,str))
          val ast = acc.injectValue(a)
          aux(t,ast,Token.Type.Quote)

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

        case Token(Token.Type.ToOperator,_,_)::tail =>
          val ast = acc.applyOperator(Token.Type.ToOperator)
          val a = ast.injectToAllow(0)
          aux(tail, a, Token.Type.ToOperator)

        case Token(Token.Type.NotOperator,_,_)::tail =>
          aux(tail,acc,Token.Type.NotOperator)

        //Identifiers
        case Token(Token.Type.Identifier, text, _) :: tail =>
          val value = Leaf[String](Parameter[String](Parameter.Type.Argument, text))
          lastToken match
            case t if Token.isAnIntegerKeyword(t) =>
              if(!Translator.isInteger(text)) then
                throw new STARException("Syntax error", s"${Token.printToken(t)} should be used with integers")
              val v = Integer.parseInt(text)
              val ast = acc.injectKeyword(v,t)
              aux(tail,ast,t)
            case Token.Type.NotOperator =>
              val n_val = Ast(value).applyNotOperator()
              val ast = acc.injectValue(n_val.tpe)
              aux(tail,ast,Token.Type.Identifier)
            case _ =>
              val ast = acc.injectValue(value)
              aux(tail, ast, Token.Type.Identifier)


        case _ => throw new STARException("Syntax Error","Parsing could not finish, error while parsing")
    val (end,_) = aux(this.input,Ast(Null),Token.Type.Null)
    end
}
