package fr.charlotte

import org.scalatest.funsuite.AnyFunSuite
import fr.charlotte.lexing.Lexer
import fr.charlotte.lexing.Token
class LexerTest extends AnyFunSuite:
  test("OrTest"){
    val result = Lexer("(C1 or C2)").lex()
    val should = List(Token(
      tpe=Token.Type.LPar,
      text="(",
      startPos = 0
    ),Token(
      tpe=Token.Type.Identifier,
      text="C1",
      startPos = 1
    ),Token(
      tpe=Token.Type.OrOperator,
      text="or",
      startPos = 4
    ),Token(
      tpe=Token.Type.Identifier,
      text="C2",
      startPos = 7
    ),Token(
      tpe=Token.Type.RPar,
      text=")",
      startPos = 9
    ),Token(
      tpe=Token.Type.EOF,
      text="<EOF>",
      startPos = 10
    ))
    assert(result == should)
  }
  test("UnicodeTest"){
    val result = Lexer("∩∪¬").lex()
    val should = List(Token(
      tpe=Token.Type.AndOperator,
      text = "∩",
      startPos = 0
    ),Token(
      tpe=Token.Type.OrOperator,
      text="∪",
      startPos = 1
    ),Token(
      tpe=Token.Type.NotOperator,
      text="¬",
      startPos = 2
    ),Token(
      tpe=Token.Type.EOF,
      text="<EOF>",
      startPos = 3
    ))
    assert(result == should)
  }
