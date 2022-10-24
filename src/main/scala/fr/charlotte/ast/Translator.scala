package fr.charlotte.ast

import Ast.Tree.*

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
          case Parameter.Type.None => Translator(tablename, tree).translate + " " + Translator(tablename, tree1)
          case Parameter.Type.Argument => Translator(tablename, tree).translate + " " + Translator(tablename, tree1)
          case Parameter.Type.OrOperator
          => s"($tablename LIKE \"%${Translator(tablename, tree).translate}%\" OR $tablename LIKE \"%${Translator(tablename, tree1).translate})"
          case Parameter.Type.AndOperator
          => s"($tablename LIKE \"%${Translator(tablename, tree).translate}%\" AND $tablename LIKE \"%${Translator(tablename, tree1).translate})"
          case Parameter.Type.NotOperator
          => s"($tablename NOT LIKE \"%${Translator(tablename, tree).translate}%\")"
        }
      case _ => ""
  }
}
