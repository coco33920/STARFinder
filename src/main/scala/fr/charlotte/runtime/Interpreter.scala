package fr.charlotte.runtime

import fr.charlotte.ast.Ast.Tree.Node
import fr.charlotte.ast.{Ast, Parameter}
import fr.charlotte.runtime.Translator
import fr.charlotte.utils.Utils
import fr.charlotte.Provider
import org.jline.terminal.Terminal

import java.util

case class Interpreter(provider: Provider, ast: Ast, terminal: Terminal) {

  /*
  TODO:
  A not dumb graph searching algorithm to connect the dots, you can do it charlotte :)
  */

  def connection(start: String, stop: String): String = {
    val startLines = provider.listOfLinesFromStopName(start)
    val stopLines = provider.listOfLinesFromStopName(stop)
    val intersect = Utils.intersectArrayList(startLines,stopLines)

    if(!intersect.isEmpty)
      var s = REPL.writeInBlue("There is direct connections from ",terminal)
      s += REPL.writeColor(178,start,terminal)
      s += REPL.writeInBlue(" to ",terminal)
      s += REPL.writeColor(178,stop,terminal)
      s += REPL.writeInBlue(" with lines ",terminal)
      val builder = String.join(",", intersect)
      s += REPL.writeColor(178,builder,terminal)
      return s
    //TODO: do smart things
    var s = REPL.writeInBlue("There is no direct connections between ",terminal)
    s += REPL.writeColor(178, start, terminal)
    s += REPL.writeInBlue(" and ", terminal)
    s += REPL.writeColor(178, stop, terminal)
    s
  }


  //It's dumb: allow 0
  def executeToOperators(s: String, s2: String): String = {
    val a1 = provider.listOfLinesFromStopName(s)
    val a2 = provider.listOfLinesFromStopName(s2)
    if a1.isEmpty then
      REPL.writeInBlue(s"There is no line attached to the name ${REPL.writeColor(178, s, terminal)}", terminal)
    else if a2.isEmpty then
      REPL.writeInBlue(s"There is no line attached to the name ${REPL.writeColor(178, s2, terminal)}", terminal)
    else
      connection(s,s2)
  }

  def unpackageToOperator(s1: Ast.Tree, s2: Ast.Tree): String = {
    (s1, s2) match
      case (Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body)), Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body1)))
      => executeToOperators(body.toString, body1.toString)
      case _ => REPL.writeColor(178, "TO ", terminal) + REPL.writeInBlue("operator only works between two strings!", terminal)
  }

  def interprete: (String, util.ArrayList[String], String) = {
    ast.tpe match
      case Node(Parameter(Parameter.Type.ToOperator, _), s1: Ast.Tree, s2: Ast.Tree)
      =>
        val s = unpackageToOperator(s1, s2)
        (s, new util.ArrayList[String](), s)
      case tpe =>
        val t = Translator(provider.tableName(), tpe).translate
        val prompt = REPL.writeInBlue("Bus stops obeying the rule : ", terminal) + REPL.writeColor(178, ast.print(), terminal)
        (prompt, provider.executeValue(t), t)
  }


}
