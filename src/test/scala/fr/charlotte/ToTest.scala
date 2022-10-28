package fr.charlotte
import org.scalatest.funsuite.AnyFunSuite
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.Lexer
import fr.charlotte.ast.Ast
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter
class ToTest extends AnyFunSuite {

  test("parsing to"){
    val p = Parser(Lexer("République to Gares").lex()).parse()
    val should = Ast(
      Node(Parameter(Parameter.Type.ToOperator, ""),
        Leaf[String](Parameter(Parameter.Type.Argument, "République")),
        Leaf[String](Parameter(Parameter.Type.Argument, "Gares")))
    )
    assert(p == should)
  }
  
  
}
