package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.categories.objects.semigroup.ValidatedSemigroup;
import org.dexenjaeger.algebra.model.ValidatedSemigroupSpec;
import org.dexenjaeger.algebra.model.ValidatingBinaryOperator;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatedSemigroupTest {
  private Map<String, Integer> createReverseLookup(String[] elements) {
    Map<String, Integer> result = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      result.put(elements[i], i);
    }
    return result;
  }
  
  @Test
  void staticInitializerTest() {
    ValidatedSemigroup.createSemigroup(new ValidatedSemigroupSpec(
        new ValidatingBinaryOperator(
          MoreArrayUtils.createArray("I"),
          Collections.singletonMap("I", 0),
          (i, j) -> 0
        )
      )
    );
  }
  
  @Test
  void getProductTest() {
    String[] elements = MoreArrayUtils.createArray("I", "a");
    ValidatedSemigroup testSemigroup = ValidatedSemigroup.createSemigroup(
      new ValidatedSemigroupSpec(
        new ValidatingBinaryOperator(
          elements,
          createReverseLookup(elements),
          (i, j) -> (i + j) % 2
        )
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
    String[] elements = MoreArrayUtils.createArray("I", "a", "b");
    ValidatedSemigroup testSemigroup = ValidatedSemigroup.createSemigroup(
      new ValidatedSemigroupSpec(
        "+",
        new ValidatingBinaryOperator(
          elements,
          createReverseLookup(elements),
          (i, j) -> (i+j) % 3
        )
      )
    );
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_+____|_I____a____b____\n")
      .append(" I    | I    a    b    \n")
      .append(" a    | a    b    I    \n")
      .append(" b    | b    I    a    \n");
    
    assertEquals(
      sb.toString(),
      testSemigroup.getMultiplicationTable()
    );
  }
  
  @Test
  void getCyclicGroupTest() {
    String[] elements = MoreArrayUtils.createArray("I", "a", "b");
    ValidatedSemigroup testSemigroup = ValidatedSemigroup.createSemigroup(
      new ValidatedSemigroupSpec(
        "+",
        new ValidatingBinaryOperator(
          elements,
          createReverseLookup(elements),
          (i, j) -> (i+j) % 3
        )
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
  
  @Test
  void invalidBinaryOperator() {
    String[] elements = {"x", "y"};
    Map<String, Integer> reverseLookup = new HashMap<>() {{
      put("x", 0);
      put("y", 1);
    }};
    RuntimeException e = assertThrows(RuntimeException.class, () -> ValidatedSemigroup.createSemigroup(new ValidatedSemigroupSpec("x", new ValidatingBinaryOperator(
      elements, reverseLookup, (a, b) -> 2
    ))));
    
    assertEquals(
      "Semigroups may only be created from valid binary operators.", e.getMessage()
    );
  }
  
  @Test
  void nonAssociativeBinaryOperator() {
    String[] elements = {"x", "y", "z"};
    int[][] product = {
      {0, 1, 2},
      {0, 0, 2},
      {1, 0, 1}
    };
    Map<String, Integer> reverseLookup = new HashMap<>() {{
      put("x", 0);
      put("y", 1);
      put("z", 2);
    }};
    RuntimeException e = assertThrows(RuntimeException.class, () -> ValidatedSemigroup.createSemigroup(new ValidatedSemigroupSpec("x", new ValidatingBinaryOperator(
      elements, reverseLookup, (a, b) -> product[a][b]
    ))));
    
    assertEquals(
      "Semigroups may only be crated from associative binary operators.", e.getMessage()
    );
  }
}