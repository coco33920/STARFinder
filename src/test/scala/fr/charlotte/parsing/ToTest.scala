package fr.charlotte.parsing

import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.{Ast, Parameter, Parser}
import fr.charlotte.lexing.Lexer
import org.scalatest.funsuite.AnyFunSuite
class ToTest extends AnyFunSuite {

  test("parsing to"){
    val p = Parser(Lexer("République to Gares").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.ToOperator, "allow:0"),
        Leaf[String](Parameter(Parameter.Type.Argument, "République")),
        Leaf[String](Parameter(Parameter.Type.Argument, "Gares")))
    )
    assert(p == should)
  }
}
