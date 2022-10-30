package fr.charlotte.parsing
import org.scalatest.funsuite.AnyFunSuite

import fr.charlotte.ast.*
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.lexing.*
class ShowTest  extends AnyFunSuite {

  test("Parse show"){
    val p = Parser(Lexer("C1 or C2 show 10").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.OrOperator,"show:10;"),
        Leaf[String](Parameter(Parameter.Type.Argument, "C1")),
        Leaf[String](Parameter(Parameter.Type.Argument, "C2")))
    )
    assert(p==should)
  }

  test("Parse show with <<") {
    val p = Parser(Lexer("C1 or C2 << 10").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.OrOperator, "show:10;"),
        Leaf[String](Parameter(Parameter.Type.Argument, "C1")),
        Leaf[String](Parameter(Parameter.Type.Argument, "C2")))
    )
    assert(p == should)
  }


  test("Parse show with limit") {
    val p = Parser(Lexer("C1 or C2 limit 10").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.OrOperator, "show:10;"),
        Leaf[String](Parameter(Parameter.Type.Argument, "C1")),
        Leaf[String](Parameter(Parameter.Type.Argument, "C2")))
    )
    assert(p == should)
  }


}
