package fr.charlotte.ast

import fr.charlotte.{Provider, REPL}
import fr.charlotte.ast.Ast.Tree.*
import org.jline.terminal.Terminal

import java.util

case class Interpreter(provider: Provider, ast: Ast,terminal: Terminal) {

  def executeToOperator(s1: Ast.Tree, s2: Ast.Tree): String ={
    (s1,s2) match
      case (Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body)),Ast.Tree.Leaf(Parameter(Parameter.Type.Argument, body1)))
        => body.toString+body1.toString//do smart things
      case _ => REPL.writeColor(178,"TO ",terminal) + REPL.writeInBlue("operator only works between two strings!",terminal)
  }

  //do smart things
  //to => deux demandes sql, intersection, etc.
  //normal => translate
  //MACHIN to TRUC
  def interprete: (String,util.ArrayList[String],String) = {
    ast.tpe match
      case Node(Parameter(Parameter.Type.ToOperator,_),s1: Ast.Tree, s2: Ast.Tree)
        =>
        val s = executeToOperator(s1,s2)
        (s,new util.ArrayList[String](),s)
      case tpe =>
        val t = Translator(provider.tableName(), tpe).translate
        val prompt = REPL.writeInBlue("Bus stops obeying the rule : ",terminal) + REPL.writeColor(178,ast.print(),terminal)
        (prompt,provider.executeValue(t),t)
  }


}
