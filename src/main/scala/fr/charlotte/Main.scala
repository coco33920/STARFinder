package fr.charlotte
import scala.io.StdIn.readLine

@main def run(): Unit =
  while true do
    println("Input line : ")
    val line = readLine()
    val lexed = Lexer(line).lex()
    println("Lexed lined : ")
    println(lexed)