package fr.charlotte.ast

import fr.charlotte.lexing.Token
import Ast.Tree.*

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
          val ast = acc.injectValue(value.tpe)
          aux(t,ast,Token.Type.LPar)

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
          lastToken match
            case Token.Type.NotOperator =>
              val value = Node[String](
                Parameter[String](Parameter.Type.NotOperator, ""),
                Leaf[String](Parameter[String](Parameter.Type.Argument, body=text)),
                Leaf[String](Parameter[String](Parameter.Type.None, body = ""))
              )
              val ast = acc.injectValue(value)
              aux(tail,ast,Token.Type.Identifier)
            case _ =>
              val value = Leaf[String](Parameter[String](Parameter.Type.Argument, text))
              val ast = acc.injectValue(value)
              aux(tail, ast, Token.Type.Identifier)


        case _ => (Ast(Null),List.empty[Token])
    val (end,_) = aux(this.input,Ast(Null),Token.Type.Null)
    end
}
