package fr.charlotte

import fr.charlotte.ast.Translator
import fr.charlotte.lexing.Lexer
import fr.charlotte.ast.Parser
import org.jline.reader.impl.DefaultParser
import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString

import scala.io.StdIn.readLine
import scala.util.control.Breaks.break

class REPL(provider: Provider,var verbose: Boolean) {
  def main : Unit = {
    val term = TerminalBuilder.builder();
    val terminal = term.build()
    val lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .parser(DefaultParser())
      .variable(LineReader.INDENTATION, 2)
      .option(LineReader.Option.INSERT_BRACKET, true)
      .build()
    terminal.writer().println(AttributedString.fromAnsi(s"\u001B[1mfoo\u001B[0m Welcome to STARFinder, provider is ${provider.implementationName()}").toAnsi(terminal))
    while true do {
      val line = lineReader.readLine("star-finder ~> ").trim
      terminal.flush()
      line match
        case "exit" => System.exit(0)
        case _ => ()
    }

  }
}