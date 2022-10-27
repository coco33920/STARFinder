package fr.charlotte.lexing

import Token.Type.*

import scala.collection.mutable

object Lexer:
  def isAndOperator(input: Char): Boolean =
    (input == '&') || (input == '∩') || (input == '-')

  def isOrOperator(input: Char): Boolean =
    (input == '|') || (input == '∪') || (input == '+')

  def isNotOperator(input: Char): Boolean =
    (input == '!') || (input == '¬')

class Lexer(input: String){
  var currentPos = 0
  var token: mutable.ArrayBuffer[Token] = mutable.ArrayBuffer.empty[Token]

  def lexOperators(nextChar: Char, tokenStartPos: Int): Boolean =
    if Lexer.isAndOperator(nextChar) then
      currentPos += 1
      token += Token(AndOperator, nextChar.toString, tokenStartPos)
      true
    else if Lexer.isOrOperator(nextChar) then
      currentPos += 1
      token += Token(OrOperator, nextChar.toString, tokenStartPos)
      true
    else if Lexer.isNotOperator(nextChar) then
      currentPos += 1
      token += Token(NotOperator, nextChar.toString, tokenStartPos)
      true
    else
      false

  def lexParenthesis(nextChar: Char, tokenStartPos: Int): Boolean =
    if nextChar == '(' then
      currentPos += 1
      token += Token(LPar, nextChar.toString, tokenStartPos)
      true
    else if nextChar == ')' then
      currentPos += 1
      token += Token(RPar, nextChar.toString, tokenStartPos)
      true
    else
      false

  def lexString(nextChar: Char, tokenStartPos: Int): Boolean =
    if nextChar.isDigit || nextChar.isLetter then
      var text = ""
      while currentPos < input.length && input(currentPos).isLetterOrDigit do
        text += input(currentPos)
        currentPos += 1
      val tpe = Token.stringToTokenType(text)
      token += Token(tpe, text, tokenStartPos)
      true
    else
      false


  def lex(): List[Token] =
    while currentPos < input.length do
      val tokenStartPos = currentPos
      val nextChar = input(currentPos)

      if nextChar.isWhitespace then
        currentPos += 1
      else if lexOperators(nextChar, tokenStartPos) then ()
      else if lexParenthesis(nextChar,tokenStartPos) then ()
      else if lexString(nextChar,tokenStartPos) then ()
      else
        throw RuntimeException(s"Unknown character $nextChar at position $currentPos")

    token += Token(EOF, "<EOF>", currentPos)
    token.toList
}
