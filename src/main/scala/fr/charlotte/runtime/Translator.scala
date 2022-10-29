package fr.charlotte.runtime

import fr.charlotte.ast.Ast.Tree.{Leaf, Node, Null}
import fr.charlotte.ast.{Ast, Parameter}
import fr.charlotte.runtime.Translator.isInteger

object Translator {
  def isInteger(body: String): Boolean = {
    try {
      Integer.parseInt(body)
      true
    } catch {
      case e: Exception => false
    }
  }
}

case class Translator(tablename: String, input: Ast.Tree) {

  def translateBody(body: Any): String ={
    if body.toString == "" then
      ""
    else if isInteger(body.toString) then
      val s = Integer.parseInt(body.toString)
      s"LIMIT ${s}"
    else
      ""
  }

  def translate: String = {
    this.input match
      case Null => ""
      case Leaf(s: Parameter[String]) => {
        s.tpe match
          case Parameter.Type.None => ""
          case Parameter.Type.Argument => s"($tablename LIKE \"%${s.body}%\")"
          case _ => ""
      }
      case Node(Parameter(ptype, body), tree: Ast.Tree, tree1: Ast.Tree) =>
        val add = translateBody(body)
        ptype match {
          case Parameter.Type.None => Translator(tablename, tree).translate + " " + Translator(tablename, tree1).translate
          case Parameter.Type.Argument => Translator(tablename, tree).translate + " " + Translator(tablename, tree1).translate
          case Parameter.Type.OrOperator
          => s"(${Translator(tablename, tree).translate} OR ${Translator(tablename, tree1).translate}) ${add}".trim
          case Parameter.Type.AndOperator
          => s"(${Translator(tablename, tree).translate} AND ${Translator(tablename, tree1).translate}) ${add}".trim
          case Parameter.Type.NotOperator
          => s"(NOT ${Translator(tablename, tree).translate}) ${add}".trim
          case _ => throw new RuntimeException("Parameter error in translation, cannot complete")
        }
      case _ => throw new RuntimeException("Parameter error in translation, cannot complete")
  }
}
