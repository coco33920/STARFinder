package fr.charlotte
import scala.collection.mutable
import Token.Type.*
object Lexer:
  def isAndOperator(input: Char): Boolean =
    (input == '&') || (input == '∩')
  def isOrOperator(input: Char): Boolean =
    (input == '|') || (input == '∪')
  def isNotOperator(input: Char): Boolean =
    (input == '!') || (input == '¬')

class Lexer(input: String) {
  def lex(): List[Token] =
    val token = mutable.ArrayBuffer.empty[Token]
    var currentPos = 0
    while currentPos < input.length do
      val tokenStartPos = currentPos
      val nextChar = input(currentPos)
      if nextChar.isWhitespace then
        currentPos += 1
      else if Lexer.isAndOperator(nextChar) then
        currentPos += 1
        token += Token(AndOperator, nextChar.toString, tokenStartPos)
      else if Lexer.isOrOperator(nextChar) then
        currentPos += 1
        token += Token(OrOperator, nextChar.toString, tokenStartPos)
      else if Lexer.isNotOperator(nextChar) then
        currentPos += 1
        token += Token(NotOperator, nextChar.toString, tokenStartPos)
      else if nextChar == '(' then
        currentPos += 1
        token += Token(LPar,nextChar.toString,tokenStartPos)
      else if nextChar == ')' then
        currentPos += 1
        token += Token(RPar,nextChar.toString,tokenStartPos)
      else if nextChar.isDigit || nextChar.isLetter then
        var text = ""
        while currentPos < input.length && input(currentPos).isLetterOrDigit do
          text += input(currentPos)
          currentPos += 1
        val tpe = text match
          case "and" => AndOperator
          case "or" => OrOperator
          case "not" => NotOperator
          case "true" => True
          case "false" => False
          case _ => Identifier
        token += Token(tpe,text,tokenStartPos)
      else
        throw RuntimeException(s"Unknown character $nextChar at position $currentPos")
    token += Token(EOF, "<EOF>", currentPos)
    token.toList
}
