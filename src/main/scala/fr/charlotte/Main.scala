package fr.charlotte
import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.Lexer
import fr.charlotte.providers.STARProvider

def getDefaultProvider: Provider = STARProvider()
@main def run(): Unit =
  val t = getDefaultProvider
  t.load();
  t.listFromName("")
  while true do
      println("Input line : ")
      val line = readLine()
      val lexed = Lexer(line).lex()
      println("Lexed lined : ")
      println(lexed)
      val parsed = Parser(lexed).parse()
      println("Parsed Line : ")
      println(parsed)
      println("Equals to : ")
      println(parsed.print())
