package fr.charlotte
import fr.charlotte.lexing.Lexer
import fr.charlotte.ast.Parser
import org.scalatest.funsuite.AnyFunSuite

import java.sql.SQLException
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


}
