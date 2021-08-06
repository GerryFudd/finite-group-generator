package org.dexenjaeger.algebra.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FiniteSemigroupTest {
  private String[] createArray(String...elements) {
    return elements;
  }
  
  private Map<String, Integer> createReverseLookup(String[] elements) {
    Map<String, Integer> result = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      result.put(elements[i], i);
    }
    return result;
  }
  
  @Test
  void staticInitializerTest() {
    FiniteSemigroup.createSemigroup(
      "*",
      new BinaryOperator(
        createArray("I"),
        Collections.singletonMap("I", 0),
        (i, j) -> 0
      )
    );
  }
  
  @Test
  void getElementsDisplayTest() {
    String[] elements = createArray("I", "a");
    FiniteSemigroup testSemigroup = FiniteSemigroup.createSemigroup(
      "*",
      new BinaryOperator(
      elements,
      createReverseLookup(elements),
      (i, j) -> (i + j) % 2
      )
    );
    assertEquals("I, a", testSemigroup.getElementsDisplay());
  }
  
  @Test
  void getProductTest() {
    String[] elements = createArray("I", "a");
    FiniteSemigroup testSemigroup = FiniteSemigroup.createSemigroup(
      "*",
      new BinaryOperator(
        elements,
        createReverseLookup(elements),
        (i, j) -> (i + j) % 2
      )
    );
    
    assertEquals(
      "I",
      testSemigroup.getProduct("I", "I")
    );
    
    assertEquals(
      "I",
      testSemigroup.getProduct("a", "a")
    );
    
    assertEquals(
      "a",
      testSemigroup.getProduct("I", "a")
    );
    
    assertEquals(
      "a",
      testSemigroup.getProduct("a", "I")
    );
    
    RuntimeException e = assertThrows(RuntimeException.class, () -> testSemigroup.getProduct("I", "b"));
    assertEquals("Element \"b\" doesn't exist in I, a", e.getMessage());
  }
  
  @Test
  void getMultiplicationTableTest() {
    String[] elements = createArray("I", "a", "b");
    FiniteSemigroup testSemigroup = FiniteSemigroup.createSemigroup(
      "+",
      new BinaryOperator(
        elements,
        createReverseLookup(elements),
        (i, j) -> (i+j) % 3
      )
    );
    StringBuilder sb = new StringBuilder();
    sb.append("\n_+_|_I_a_b_\n");
    sb.append(" I | I a b \n");
    sb.append(" a | a b I \n");
    sb.append(" b | b I a \n");
    
    assertEquals(
      sb.toString(),
      testSemigroup.getMultiplicationTable()
    );
  }
  
  @Test
  void getCyclicGroupTest() {
    String[] elements = createArray("I", "a", "b");
    FiniteSemigroup testSemigroup = FiniteSemigroup.createSemigroup(
      "+",
      new BinaryOperator(
        elements,
        createReverseLookup(elements),
        (i, j) -> (i+j) % 3
      )
    );
    
    assertEquals(
      List.of("a", "b", "I"),
      testSemigroup.getCyclicGroup("a")
    );
    
    assertEquals(
      List.of("b", "a", "I"),
      testSemigroup.getCyclicGroup("b")
    );
  }
}