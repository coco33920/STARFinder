package fr.charlotte.ast

import fr.charlotte.Provider
import fr.charlotte.ast.Ast.Tree.*

import java.util

case class Interpreter(provider: Provider, ast: Ast) {
  //do smart things
  //to => deux demandes sql, intersection, etc.
  //normal => translate
  //MACHIN to TRUC
  def interprete: (util.ArrayList[String],String) = {
    ast.tpe match
      case Node(Parameter(Parameter.Type.ToOperator,_),s1: Ast.Tree, s2: Ast.Tree)
        => (new util.ArrayList[String](),"TO!")
      case tpe => 
        val t = Translator(provider.tableName(), tpe).translate
        (provider.executeValue(t),t)
  }
  
  
}
