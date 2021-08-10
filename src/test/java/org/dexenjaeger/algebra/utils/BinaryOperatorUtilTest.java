package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.binaryoperator.ConcreteBinaryOperator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryOperatorUtilTest {
  @Test
  void getMultiplicationTableTest() {
    String[] elements = {"a", "b", "c"};
    
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_+____|_a____b____c____\n")
      .append(" a    | a    b    c    \n")
      .append(" b    | b    c    a    \n")
      .append(" c    | c    a    b    \n");
    
    
    assertEquals(
      sb.toString(),
      BinaryOperatorUtil.printMultiplicationTable(
        ConcreteBinaryOperator.builder()
          .operatorSymbol("+")
          .elements(elements)
          .lookup(Map.of("a", 0, "b", 1, "c", 2))
          .operator((a, b) -> (a + b) % 3)
          .build()
      )
    );
  }
  
  @Test
  void getCycleTest() {
    String[] elements = {"a", "b", "c"};
  
    BiFunction<String, String, String> operator = BinaryOperatorUtil.createOperator(
      elements, (a, b) -> (a + b) % 3
    );
    
    assertEquals(
      List.of("b", "c", "a"),
      BinaryOperatorUtil.getCycle("b", operator)
    );
    
    assertEquals(
      List.of("c", "b", "a"),
      BinaryOperatorUtil.getCycle("c", operator)
    );
    
    assertEquals(
      List.of("a"),
      BinaryOperatorUtil.getCycle("a", operator)
    );
  }
}