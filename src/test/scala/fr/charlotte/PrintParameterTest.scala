package fr.charlotte
import org.scalatest.funsuite.AnyFunSuite

import fr.charlotte.ast.Parameter
import fr.charlotte.ast.Parameter.Type.*

class PrintParameterTest extends AnyFunSuite {

  test("Print Operators"){
    val or = Parameter(OrOperator,"")
    val and = Parameter(AndOperator, "")
    val not = Parameter(NotOperator, "")
    val is = or.print + and.print + not.print
    val should = "∪∩¬"
    assert(is.equalsIgnoreCase(should))
  }
  
  test("Print Argument"){
    val param = Parameter(Argument, "test")
    val is = param.print
    val should = "test"
    assert(is.equalsIgnoreCase(should))
  }
  
}
