package fr.charlotte.lexing

import Token.Type.*
import fr.charlotte.lexing.Lexer.isAnAllowedCharacter

import scala.collection.mutable

object Lexer:

  def isAnAllowedCharacter(input: Char): Boolean =
    input.isLetterOrDigit || input == '>' || input == '-'

  def isAndOperator(input: Char): Boolean =
    (input == '&') || (input == '∩')

  def isOrOperator(input: Char): Boolean =
    (input == '|') || (input == '∪') || (input == '+')

  def isNotOperator(input: Char): Boolean =
    (input == '!') || (input == '¬')

  def isToOperator(input: Char): Boolean =
    (input == '→')

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
    else if Lexer.isToOperator(nextChar) then
      currentPos += 1
      token += Token(ToOperator, nextChar.toString, tokenStartPos)
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
    if isAnAllowedCharacter(nextChar) then
      var text = ""
      while currentPos < input.length && (isAnAllowedCharacter(input(currentPos))) do
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
      else if nextChar == '"' then
        currentPos += 1
        token += Token(Token.Type.Quote, "", tokenStartPos)
      else
        throw new RuntimeException(s"Lexing cannot complete, unknown character ${input(currentPos)} at position ${currentPos}")

    token += Token(EOF, "<EOF>", currentPos)
    token.toList
}
