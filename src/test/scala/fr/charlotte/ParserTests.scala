package fr.charlotte


import org.scalatest.funsuite.AnyFunSuite
import fr.charlotte.lexing.Lexer
import fr.charlotte.lexing.Token
import fr.charlotte.ast.Parser
import fr.charlotte.ast.Ast
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter
import fr.charlotte.ast.Parameter.Type.*

class ParserTests extends AnyFunSuite{
  test("Simple command test"){
    val result = Parser(Lexer("C1 + C2").lex()).parse()
    val should = Ast(
      Node(
        Parameter(Parameter.Type.OrOperator, ""),
        Leaf(Parameter[String](Parameter.Type.Argument, "C1")),
        Leaf(Parameter[String](Parameter.Type.Argument, "C2"))
      )
    )
    assert(result == should)
  }
  test("Complex Command Test"){
    val result = Parser(Lexer("(C1 or C2) and b").lex()).parse()
    val should = Ast(
      Node(
        Parameter(Parameter.Type.AndOperator, ""),
        Node(Parameter(Parameter.Type.OrOperator, ""),
          Leaf(Parameter(Parameter.Type.Argument, "C1")),
          Leaf(Parameter(Parameter.Type.Argument, "C2"))
        ),
        Leaf(Parameter(Parameter.Type.Argument, "b"))
      )
    )
    assert(result == should)
  }

  test("Three tests"){
    val result = Parser(Lexer("C1 or C2 or C3").lex()).parse()
    val should = Ast(
      Node(
        Parameter(Parameter.Type.OrOperator, ""),
        Node(Parameter(Parameter.Type.OrOperator, ""),
          Leaf(Parameter(Parameter.Type.Argument, "C1")),
          Leaf(Parameter(Parameter.Type.Argument, "C2"))
        ),
        Leaf(Parameter(Parameter.Type.Argument, "C3"))
      )
    )
    assert(result == should)
  }

  test("Not Test"){
    val result = Parser(Lexer("not C1 or C2").lex()).parse()
    val should = Ast(
      Node(
        Parameter(Parameter.Type.OrOperator, ""),
        Node(Parameter(Parameter.Type.NotOperator, ""),
          Leaf(Parameter(Parameter.Type.Argument, "C1")),
          Leaf(Parameter(Parameter.Type.None, ""))
        ),
        Leaf(Parameter(Parameter.Type.Argument, "C2"))
      )
    )
    assert(result == should)
  }

  test("Not Value Test"){
    val result = Parser(Lexer("not (C1 or C2)").lex()).parse()
    val should = Ast(
      Node(
        Parameter(Parameter.Type.NotOperator, ""),
        Node(Parameter(Parameter.Type.OrOperator, ""),
          Leaf[String](Parameter(Parameter.Type.Argument, "C1")),
          Leaf[String](Parameter(Parameter.Type.Argument, "C2"))
        ),
        Leaf[String](Parameter(Parameter.Type.None, ""))
      )
    )
    assert(result == should)
  }

}
