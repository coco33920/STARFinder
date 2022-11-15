package fr.charlotte.runtime

import fr.charlotte.ast.Ast.Tree.Node
import fr.charlotte.ast.{Ast, Parameter}
import fr.charlotte.runtime.Translator
import fr.charlotte.utils.Utils
import fr.charlotte.Provider
import org.jline.terminal.Terminal

import scala.jdk.CollectionConverters.*
import java.util
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.control.Breaks.break

case class Interpreter(provider: Provider, ast: Ast, terminal: Terminal) {

  /*
  TODO:
  A not dumb graph searching algorithm to connect the dots, you can do it charlotte :)
  */

  def connection(start: String, end: String, maxDepth: Int, using: String): String = {
    val startLines = provider.listOfLinesFromStopName(start)
    val endLines = provider.listOfLinesFromStopName(end)
    val intersect = Utils.intersectArrayList(startLines, endLines)
    val usingList = util.ArrayList[String]
    if (!using.equalsIgnoreCase(""))
      using.split(",").foreach(e => usingList.add(e))
    val intersectUsing = Utils.intersectArrayList(intersect, usingList)
    if (!intersect.isEmpty) then {
      if (!usingList.isEmpty) then {
        if (intersectUsing.isEmpty) then {
          var s = REPL.writeInBlue("There is no direct connection from ", terminal)
          s += REPL.writeColor(178, start, terminal)
          s += REPL.writeInBlue(" to ", terminal)
          s += REPL.writeColor(178, end, terminal)
          s += REPL.writeInBlue(" using line(s) : ", terminal)
          s += REPL.writeColor(178, String.join(",", intersectUsing), terminal)
          s
        } else {
          var s = REPL.writeInBlue("There is a direct connection from ", terminal)
          s += REPL.writeColor(178, start, terminal)
          s += REPL.writeInBlue(" to ", terminal)
          s += REPL.writeColor(178, end, terminal)
          s += REPL.writeInBlue(" using line(s) : ", terminal)
          s += REPL.writeColor(178, String.join(",", intersectUsing), terminal)
          s
        }
      } else {
        var s = REPL.writeInBlue("There is a direct connection from ", terminal)
        s += REPL.writeColor(178, start, terminal)
        s += REPL.writeInBlue(" to ", terminal)
        s += REPL.writeColor(178, end, terminal)
        s += REPL.writeInBlue(" using line(s) : ", terminal)
        s += REPL.writeColor(178, String.join(",", intersect), terminal)
        s
      }
    } else {

      var startList = startLines;
      if (!usingList.isEmpty) {
        startList = Utils.intersectArrayList(startLines, usingList)
      }
      if (startList.isEmpty) {
        return REPL.writeColor(160, "Error while running : ", terminal) + REPL.writeInBlue(s"${REPL.writeColor(178, start, terminal)} ${REPL.writeInBlue("does not have any of the specified lines", terminal)}", terminal)
      }

      def calculate(line: String, acc: List[(String, String)], depth: Int): List[(String, String)] = {
        if (depth > maxDepth)
          return List.empty[(String, String)]
        val list_of_stops = provider.listOfStopsFromLineName(line)
        val list_of_connections = provider.listOfConnectionsFromLine(line)
        if (list_of_stops.contains(end)) then {
          (line, end) :: acc
        } else {
          var b = List.empty[List[(String, String)]]
          for ((lin, stop) <- list_of_connections.asScala) {
            val c = calculate(lin, (lin, stop.get(0)) :: acc, depth + 1);
            if (c.nonEmpty)
              b = c :: b
          }
          var res = List.empty[List[(String, String)]]
          var minSize = Int.MaxValue
          for (l <- b) {
            if l.size < minSize then
              minSize = l.size
          }
          for (l <- b) {
            if l.size == minSize then
              res = l :: res
          }
          if (res.isEmpty)
            List.empty[(String, String)]
          else
            val ret = res.map(l => (line, l.reverse.mkString(";")))
            ret
        }
      }

      val list_of_path = startList.asScala.map(f => (calculate(f, List.empty[(String, String)], 0)))

      def calculatePathForASingleTuple(ligne: String, hops: String): String = {
        val s: Array[String] = hops.split(";")
        val t: Array[(String, String)] =
          s.filter(f => f.split(",").length > 1).map(elem => {
            val v = elem.split(",")
            (v(0).replace("(", ""), v(1).replace(")", ""))
          })
        val b = mutable.StringBuilder(s"${ligne} @ ${start} => ")
        for (i <- 0 until t.length - 1) {
          val (v, s) = t(i)
          b.append(s"${s} @ ${v} => ")
        }
        val (_, v2) = t(t.length - 1);
        b.append(s"${v2}")
        b.toString()
      }

      def calculatePath(l: List[(String, String)]): String = {
        val b = mutable.StringBuilder();
        for ((v, s) <- l) {
          b.append(calculatePathForASingleTuple(v, s) + "\n")
        }
        b.toString();
      }

      if (list_of_path.flatten.isEmpty)
        REPL.writeInBlue("There is no ", terminal) + (if maxDepth == 0 then (REPL.writeInBlue("direct ", terminal)) else "") + REPL.writeInBlue("connections between ", terminal) + REPL.writeColor(178, start, terminal) + REPL.writeInBlue(" and ", terminal) + REPL.writeColor(178, end, terminal) + (if maxDepth > 0 then (REPL.writeInBlue(" within ", terminal) + REPL.writeColor(178, maxDepth.toString, terminal) + REPL.writeInBlue(" hops.", terminal)) else "")
      else
        val p = if using.equalsIgnoreCase("") then "" else REPL.writeInBlue("and using : ", terminal) + REPL.writeColor(178, String.join(",", usingList), terminal)
        REPL.writeInBlue("To go from ", terminal) + REPL.writeColor(178, start, terminal) + REPL.writeInBlue(" to ", terminal) + REPL.writeColor(178, end, terminal) + REPL.writeInBlue(s" within ${REPL.writeColor(178, maxDepth.toString, terminal)} ${REPL.writeInBlue("hops", terminal)} $p ${REPL.writeInBlue("\nYou can take the following paths :\n", terminal)}" + REPL.writeColor(178, list_of_path.map(m => calculatePath(m)).mkString("\n"), terminal), terminal)

    }
  }


  //It's dumb: allow 0
  def executeToOperators(s: String, s2: String, maxDepth: Int, using: String): String = {
    val a1 = provider.listOfLinesFromStopName(s)
    val a2 = provider.listOfLinesFromStopName(s2)
    if a1.isEmpty then
      REPL.writeInBlue(s"There is no line attached to the name ${REPL.writeColor(178, s, terminal)}", terminal)
    else if a2.isEmpty then
      REPL.writeInBlue(s"There is no line attached to the name ${REPL.writeColor(178, s2, terminal)}", terminal)
    else
      connection(s, s2, maxDepth, using.trim)
  }

  def unpackageToOperator(s1: Ast.Tree, s2: Ast.Tree, body: String): String = {
    var maxDepth = 0
    var using = ""
    if (body.contains("allow:")) {
      try {
        maxDepth = Integer.parseInt(body.split(";").filter(s => s.contains("allow:"))(0).split(":")(1))
      } catch {
        case _: Exception => maxDepth = 0
      }
    }
    if (body.contains("using:")) {
      using = body.split(";").filter(s => s.contains("using:"))(0).split(":")(1)
    }
    (s1, s2) match
      case (Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body)), Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body1)))
      => executeToOperators(body.toString, body1.toString, maxDepth, using)
      case _ => REPL.writeColor(178, "TO ", terminal) + REPL.writeInBlue("operator only works between two strings!", terminal)
  }

  def interprete: (String, util.ArrayList[String], String) = {
    ast.tpe match
      case Node(Parameter(Parameter.Type.ToOperator, body: String), s1: Ast.Tree, s2: Ast.Tree)
      =>
        val s = unpackageToOperator(s1, s2, body)
        (s, new util.ArrayList[String](), s)
      case tpe =>
        val t = Translator(provider.tableName(), tpe).translate
        val prompt = REPL.writeInBlue("Bus stops obeying the rule : ", terminal) + REPL.writeColor(178, ast.print(), terminal)
        (prompt, provider.executeValue(t), t)
  }


}
