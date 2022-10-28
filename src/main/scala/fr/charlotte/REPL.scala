package fr.charlotte

import fr.charlotte.ast.{Interpreter, Parser, Translator}
import fr.charlotte.lexing.{Lexer, Token}
import org.jline.reader.impl.DefaultParser
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.utils.AttributedString
import REPL.*
import scala.io.StdIn.readLine
import scala.util.control.Breaks.break
object REPL {
  def writeInBlue(s: String, t: Terminal): String = {
    AttributedString.fromAnsi(s"\u001B[38;5;39m${s}\u001B[0m").toAnsi(t)
  }

  def writeColor(i: Int, s: String, t: Terminal): String = {
    AttributedString.fromAnsi(s"\u001B[38;5;${i}m${s}\u001B[0m").toAnsi(t)
  }
}

class REPL(provider: Provider,var verbose: Boolean) {
  def main() : Unit = {
    val term = TerminalBuilder.builder()
    val terminal = term.build()
    val lines = provider.exposeAllLines()
    lines.add("verbose")
    lines.add("info")
    lines.add("exit")
    lines.add("or")
    lines.add("and")
    lines.add("not")
    val completer = StringsCompleter(lines)
    val lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .parser(DefaultParser())
      .variable(LineReader.INDENTATION, 2)
      .option(LineReader.Option.INSERT_BRACKET, true)
      .build()
    terminal.writer().println(AttributedString.fromAnsi(s"\u001B[38;5;39mWelcome to STARFinder, type \u001B[0m\u001B[38;5;219mhelp\u001B[0m\u001B[38;5;39m for help, service provider is \u001B[0m\u001B[38;5;178m${provider.implementationName()}\u001B[0m").toAnsi(terminal))
    while true do {
      val line = lineReader.readLine(writeColor(219, "star-finder ~> ",terminal)).trim
      terminal.flush()
      var continue = true
      line match
        case "exit" => System.exit(0)
        case "help" =>
          terminal.writer().println(writeInBlue("~~ Star-Finder ~~",terminal))
          terminal.writer().print(writeColor(178,"exit : ",terminal))
          terminal.writer().println("Exit the program")
          terminal.writer().print(writeColor(178, "verbose : ", terminal))
          terminal.writer().println("Toggle the verbose on/off")
          terminal.writer().print(writeColor(178, "info : ", terminal))
          terminal.writer().println("Prints information about the program")
          continue = false
        case "verbose" =>
          verbose = !verbose
          terminal.writer().print(writeInBlue("You toggled the verbose ", terminal))
          terminal.writer().println(writeColor(178,if verbose then "ON" else "OFF", terminal))
          continue = false
        case "info" =>
          continue = false
          terminal.writer().print(writeInBlue("STAR-Finder version 1.1.1 made by ", terminal))
          terminal.writer().println(writeColor(178, "Charlotte Thomas @ ISTIC Univ-Rennes1", terminal))
          terminal.writer().print(writeInBlue("You're using the backend provided by ", terminal))
          terminal.writer().println(writeColor(178, provider.implementationName(), terminal))
          terminal.writer().print(writeInBlue("For the town of ", terminal))
          terminal.writer().println(writeColor(178, provider.townName(), terminal))
        case _ => ()
      if continue then
        val lexed = Lexer(line).lex()
        val parsed = Parser(lexed).parse()
        val (prompt,executed,translated) = Interpreter(provider,parsed,terminal).interprete
        if(verbose){
          terminal.writer().println(writeInBlue("Lexed code", terminal))
          terminal.writer().println(writeColor(178, lexed.toString(), terminal))
          terminal.writer().println(writeInBlue("Parsed code", terminal))
          terminal.writer().println(writeColor(178, parsed.toString, terminal))
          terminal.writer().println(writeInBlue("Translated code", terminal))
          terminal.writer().println(writeColor(178, translated, terminal))
        }
        terminal.writer().println(prompt)
        terminal.writer().println(writeColor(178, executed.toString, terminal))
    }

  }
}