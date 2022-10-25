package fr.charlotte
import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.Lexer
import fr.charlotte.providers.STARProvider
import fr.charlotte.ast.Translator
def getDefaultProvider: Provider = STARProvider()
@main def run(): Unit =
  val t = getDefaultProvider
  t.load();
  while true do
      println("Input line : ")
      val line = readLine()
      val lexed = Lexer(line).lex()
      println("Lexed lined : ")
      println(lexed)
      val parsed = Parser(lexed).parse()
      println("Parsed Line : ")
      println(parsed)
      val translator = Translator(t.tableName(),parsed.tpe).translate
      val p = t.executeValue(translator)
      println("List of stops obeying to " + parsed.print())
      println(p)
