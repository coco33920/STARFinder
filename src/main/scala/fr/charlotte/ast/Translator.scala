package fr.charlotte.ast

import Ast.Tree.*
import fr.charlotte.Provider
case class Translator(tablename: String, input: Ast.Tree) {
  def translate: String = {
    this.input match
      case Null => ""
      case Leaf(s: Parameter[String]) => {
        s.tpe match
          case Parameter.Type.None => ""
          case Parameter.Type.Argument => s"($tablename LIKE \"%${s.body}%\")"
          case _ => ""
      }
      case Node(value: Parameter[_], tree: Ast.Tree, tree1: Ast.Tree) =>
        value.tpe match {
          case Parameter.Type.None => Translator(tablename, tree).translate + " " + Translator(tablename, tree1).translate
          case Parameter.Type.Argument => Translator(tablename, tree).translate + " " + Translator(tablename, tree1).translate
          case Parameter.Type.OrOperator
          => s"(${Translator(tablename, tree).translate} OR ${Translator(tablename, tree1).translate})"
          case Parameter.Type.AndOperator
          => s"(${Translator(tablename, tree).translate} AND ${Translator(tablename, tree1).translate})"
          case Parameter.Type.NotOperator
          => s"(NOT ${Translator(tablename, tree).translate})"
          case _ => "Error"
        }
      case _ => "Error"
  }
}
