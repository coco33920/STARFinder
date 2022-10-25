package fr.charlotte

import fr.charlotte.ast.Translator
import fr.charlotte.lexing.Lexer
import fr.charlotte.ast.Parser

import scala.io.StdIn.readLine

class REPL(provider: Provider,verbose: Boolean) {
  def main: Unit = {
    println("Welcome to STARFinder 1.0-SNAPSHOT you're currently using the "+provider.implementationName() + " backend.")
    while(true) {
      println("What do you want to execute")
      val t = readLine()
      val lexed = Lexer(t).lex()
      if this.verbose then
        println("Lexed code : ")
        println(lexed)
      val parsed = Parser(lexed).parse()
      if this.verbose then
        println("Parsed code : ")
        println(parsed)
      val translated = Translator(provider.tableName(), parsed.tpe).translate
      if this.verbose then
        println("Translated code : ")
        println(translated)
      println("List of stops obeying to " + parsed.print())
      println(provider.executeValue(translated))
    }
  }
}