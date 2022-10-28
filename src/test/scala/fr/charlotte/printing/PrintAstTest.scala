package fr.charlotte.printing

import fr.charlotte.ast.Parser
import fr.charlotte.lexing.Lexer
import org.scalatest.funsuite.AnyFunSuite

class PrintAstTest extends AnyFunSuite {

  test("Print a command"){
    val ast = Parser(Lexer("not (C1 or C2)").lex()).parse()
    val is = ast.print()
    val should = "¬((C1 ∪ C2))"
    assert(is.equalsIgnoreCase(should))
  }

  test("another command"){
    val ast = Parser(Lexer("(C1 and not C2)").lex()).parse()
    val is = ast.print()
    val should = "(C1 ∩ ¬(C2))"
    assert(is.equalsIgnoreCase(should))
  }


}
