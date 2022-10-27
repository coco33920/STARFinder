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
    terminal.writer().println(AttributedString.fromAnsi(s"\u001B[38;5;39mWelcome to STARFinder, provided by \u001B[0m\u001B[38;5;178m${provider.implementationName()}\u001B[0m").toAnsi(terminal))
    while true do {
      val line = lineReader.readLine(AttributedString.fromAnsi("\u001B[38;5;219mstar-finder ~> \u001B[0m\u001B[1m").toAnsi()).trim
      terminal.flush()
      line match
        case "exit" => System.exit(0)
        case _ => ()
    }

  }
}