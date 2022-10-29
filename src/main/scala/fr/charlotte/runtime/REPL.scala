package fr.charlotte.runtime

import fr.charlotte.ast.{Ast, Parser}
import fr.charlotte.config.Utils
import fr.charlotte.lexing.{Lexer, Token}
import fr.charlotte.runtime.REPL.*
import fr.charlotte.runtime.{Interpreter, Translator}
import fr.charlotte.{Provider, STARException, runtime}
import org.jline.reader.impl.DefaultParser
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.utils.AttributedString

import java.util
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
    provider.load()
    val term = TerminalBuilder.builder()
    val terminal = term.build()
    val lines = provider.exposeAllLines()
    val stops = provider.exposeAllStops()
    lines.addAll(stops)
    lines.add("verbose")
    lines.add("info")
    lines.add("exit")
    lines.add("or")
    lines.add("and")
    lines.add("not")
    lines.add("to")
    lines.add("allow")
    val completer = StringsCompleter(lines)
    val lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .parser(DefaultParser())
      .variable(LineReader.INDENTATION, 2)
      .option(LineReader.Option.INSERT_BRACKET, true)
      .build()
    terminal.writer().println(AttributedString.fromAnsi(s"\u001B[38;5;39mWelcome to STARFinder v${Utils.VERSION}, type \u001B[0m\u001B[38;5;219mhelp\u001B[0m\u001B[38;5;39m for help, service provider is \u001B[0m\u001B[38;5;178m${provider.implementationName()}\u001B[0m").toAnsi(terminal))
    while true do {
      val line = lineReader.readLine(writeColor(219, "star-finder ~> ",terminal)).trim
      terminal.flush()
      var continue = true
      line match
        case "exit" =>
          terminal.writer().println(writeInBlue("~~ Goodbye ~~", terminal))
          System.exit(0)
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
          terminal.writer().print(writeInBlue(s"STAR-Finder version ${Utils.VERSION} made by ", terminal))
          terminal.writer().println(writeColor(178, "Charlotte Thomas @ ISTIC Univ-Rennes1", terminal))
          terminal.writer().print(writeInBlue("You're using the backend provided by ", terminal))
          terminal.writer().println(writeColor(178, provider.implementationName(), terminal))
          terminal.writer().print(writeInBlue("For the town of ", terminal))
          terminal.writer().println(writeColor(178, provider.townName(), terminal))
        case _ => ()
      if continue then
        var c = true
        var lexed: List[Token] = null
        try {
          lexed = Lexer(line).lex()
        }catch{
          case s1: STARException =>
            terminal.writer().print(writeColor(160,s1.getName, terminal))
            terminal.writer().println(writeInBlue(s1.getMessage, terminal))
            c = false
        }
        if c then
          var levels = true
          var level = true
          var parsed: Ast = null
          try {
            parsed = Parser(lexed).parse()
          }catch{
            case e: STARException =>
              terminal.writer().print(writeColor(160,e.getName,terminal))
              terminal.writer().println(writeInBlue(e.getMessage, terminal))
              levels = false
          }
          if(levels) then
            var prompt: String = ""
            var executed: util.ArrayList[String] = null
            var translated: String = ""
            try {
             val (p,e,t) = runtime.Interpreter(provider, parsed, terminal).interprete
              prompt = p
              executed = e
              translated = t
            }catch{
              case e: STARException =>
                terminal.writer().print(writeColor(160,e.getName,terminal))
                terminal.writer().println(writeInBlue(e.getMessage, terminal))
                level = false
            }
            if level then
              if(verbose){
                terminal.writer().println(writeInBlue("Lexed code", terminal))
                terminal.writer().println(writeColor(178, lexed.toString(), terminal))
                terminal.writer().println(writeInBlue("Parsed code", terminal))
                terminal.writer().println(writeColor(178, parsed.toString, terminal))
                terminal.writer().println(writeInBlue("Translated code", terminal))
                terminal.writer().println(writeColor(178, translated, terminal))
                terminal.writer().println(writeInBlue("Execution", terminal))
                terminal.writer().println("")
              }
              terminal.writer().println(prompt)
              if(!executed.isEmpty)
                terminal.writer().println(writeColor(178, executed.toString, terminal))
              else
                terminal.writer().println(writeInBlue("No output available please retry", terminal))
    }

  }
}