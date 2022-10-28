package fr.charlotte.parsing

import fr.charlotte.STARException
import fr.charlotte.ast.*
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.lexing.Lexer
import org.scalatest.funsuite.AnyFunSuite

class AllowTest  extends AnyFunSuite{

  test("Error with allow"){
    try {
      Parser(Lexer("allow 5").lex()).parse()
      throw new Exception()
    }catch{
      case _: STARException =>
        ()
      case _: Exception =>
        throw new RuntimeException("Wrong Exception")
    }
  }

  test("Another allow error"){
    try{
      Parser(Lexer("allow notanumber").lex()).parse()
      throw new Exception()
    }catch{
      case _: STARException => ()
      case _: Exception => throw new RuntimeException("Wrong Exception")
    }
  }

  test("Parsing allow"){
    val is = Parser(Lexer("République to Gares allow 3").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.ToOperator, 3),
        Leaf[String](Parameter(Parameter.Type.Argument, "République")),
        Leaf[String](Parameter(Parameter.Type.Argument, "Gares")))
    )
    assert(is == should)
  }


}
