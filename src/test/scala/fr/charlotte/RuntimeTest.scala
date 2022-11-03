package fr.charlotte

import org.scalatest.funsuite.AnyFunSuite
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.Lexer
import fr.charlotte.runtime.Translator

class RuntimeTest extends AnyFunSuite {
  test("Test Or Database for STAR") {
    val p = runtime.Translator("lignes", Parser(Lexer("C1 or C2").lex()).parse().tpe).translate
    val should = "((lignes like \"%C1%\") OR (lignes LIKE \"%C2%\"))"
    assert(p.equalsIgnoreCase(should))
  }
  test("Nested And Database for STAR") {
    val p = runtime.Translator("lignes", Parser(Lexer("C1 and C2 and C3").lex()).parse().tpe).translate
    val should = "(((lignes LIKE \"%C1%\") AND (lignes LIKE \"%C2%\")) AND (lignes LIKE \"%C3%\"))"
    assert(p.equalsIgnoreCase(should))
  }
  test("Test with a limit") {
    val s = Parser(Lexer("C1 or C2 show 5").lex()).parse()
    val p = Translator("lignes", Parser(Lexer("C1 or C2 show 5").lex()).parse().tpe).translate
    val should = "((lignes LIKE \"%C1%\") OR (lignes LIKE \"%C2%\")) LIMIT 5"
    assert(p.equalsIgnoreCase(should))
  }


}
