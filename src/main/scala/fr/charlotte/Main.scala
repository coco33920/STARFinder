package fr.charlotte
import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.lexing.Lexer
@main def run(): Unit =
  while true do
    println("Input line : ")
    val line = readLine()
    val lexed = Lexer(line).lex()
    println("Lexed lined : ")
    println(lexed)
