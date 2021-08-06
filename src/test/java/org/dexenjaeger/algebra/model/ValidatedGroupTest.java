package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ValidatedGroupTest {
  @Test
  void staticInitializerTest() {
    ValidatedGroup.createGroup(
      Collections.singletonMap("I", "I"),
      new ValidatedBinaryOperator(
        MoreArrayUtils.createArray("I"),
        Collections.singletonMap("I", 0),
        (i, j) -> 0
      ),
      binOp -> mock(Monoid.class)
    );
  }
  
  @Test
  void invalidInverses() {
    int[][] product = {
      {0, 1, 2, 3},
      {1, 1, 1, 1},
      {2, 2, 2, 2},
      {3, 1, 2, 0}
    };
    
    RuntimeException e = assertThrows(RuntimeException.class, () -> ValidatedGroup.createGroup(
      Map.of("I", "I", "a", "b", "b", "a", "c", "c"),
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> product[i][j]
      ),
      binOp -> mock(Monoid.class)
    ));
    
    assertEquals(
      "Monoids may only be created with valid inverses for all elements.", e.getMessage()
    );
  }
  
  @Test
  void validInverses() {
    ValidatedGroup result = ValidatedGroup.createGroup(
      Map.of("I", "I", "a", "a3", "a2", "a2", "a3", "a"),
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> (i + j) % 4
      ),
      binOp -> ValidatedMonoid.createMonoid(
        "I",
        binOp,
        bop -> ValidatedSemigroup.createSemigroup("*", bop)
      )
    );
    
    result.getElementsAsList().forEach(element -> assertEquals(
      result.getIdentity(),
      result.getProduct(
        element,
        result.getInverse(element)
      )
    ));
  }
}