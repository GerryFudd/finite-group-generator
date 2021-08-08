package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.categories.objects.semigroup.ConcreteSemigroup;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcreteSemigroupTest {
  
  @Test
  void getProductTest() {
    Semigroup testSemigroup = ConcreteSemigroup.builder()
      .elements(Set.of("I", "a"))
      .operator((a, b) -> a.equals(b) ? "I" : "a")
      .build();
    
    assertEquals(
      "I",
      testSemigroup.prod("I", "I")
    );
    
    assertEquals(
      "I",
      testSemigroup.prod("a", "a")
    );
    
    assertEquals(
      "a",
      testSemigroup.prod("I", "a")
    );
    
    assertEquals(
      "a",
      testSemigroup.prod("a", "I")
    );
  }
  
  @Test
  void getMultiplicationTableTest() {
    String[] elements = {"a", "b", "c"};
    
    Semigroup testSemigroup = ConcreteSemigroup.builder()
      .operatorSymbol("+")
      .elements(Set.of(elements))
      .operator(BinaryOperatorUtil.createOperator(
        elements, (a, b) -> (a + b) % 3
      ))
      .build();
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_+____|_a____b____c____\n")
      .append(" a    | a    b    c    \n")
      .append(" b    | b    c    a    \n")
      .append(" c    | c    a    b    \n");
    
    assertEquals(
      sb.toString(),
      testSemigroup.getMultiplicationTable()
    );
  }
  
  @Test
  void getCycleTest() {
    String[] elements = {"a", "b", "c"};
    Semigroup testSemigroup = ConcreteSemigroup.builder()
      .elements(Set.of(elements))
                                .operator(BinaryOperatorUtil.createOperator(
                                  elements, (a, b) -> (a + b) % 3
                                ))
      .build();
    
    assertEquals(
      List.of("b", "c", "a"),
      testSemigroup.getCycle("b")
    );
    
    assertEquals(
      List.of("c", "b", "a"),
      testSemigroup.getCycle("c")
    );
    
    assertEquals(
      List.of("a"),
      testSemigroup.getCycle("a")
    );
  }
}