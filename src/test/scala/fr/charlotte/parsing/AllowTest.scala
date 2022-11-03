package fr.charlotte.parsing

import fr.charlotte.STARException
import fr.charlotte.ast.*
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.lexing.Lexer
import org.scalatest.funsuite.AnyFunSuite

class AllowTest extends AnyFunSuite {

  test("Error with allow") {
    try {
      Parser(Lexer("allow 5").lex()).parse()
      throw new Exception()
    } catch {
      case _: STARException =>
        ()
      case _: Exception =>
        throw new RuntimeException("Wrong Exception")
    }
  }

  test("Another allow error") {
    try {
      Parser(Lexer("allow notanumber").lex()).parse()
      throw new Exception()
    } catch {
      case _: STARException => ()
      case _: Exception => throw new RuntimeException("Wrong Exception")
    }
  }

  test("Parsing allow") {
    val is = Parser(Lexer("République to Gares allow 3").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.ToOperator, "allow:3;"),
        Leaf[String](Parameter(Parameter.Type.Argument, "République")),
        Leaf[String](Parameter(Parameter.Type.Argument, "Gares")))
    )
    assert(is == should)
  }

  test("Parsing allow and list of lines") {
    val is = Parser(Lexer("Gares to Tournebride allow 1 using a,C1").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.ToOperator, "allow:1;using:a,C1;"),
        Leaf[String](Parameter(Parameter.Type.Argument, "Gares")),
        Leaf[String](Parameter(Parameter.Type.Argument, "Tournebride")))
    )
  }


}
