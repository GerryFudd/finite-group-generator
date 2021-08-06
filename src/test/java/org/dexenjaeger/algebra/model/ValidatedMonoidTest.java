package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ValidatedMonoidTest {
  @Test
  void staticInitializerTest() {
    ValidatedMonoid.createMonoid(
      "I",
      new ValidatedBinaryOperator(
        MoreArrayUtils.createArray("I"),
        Collections.singletonMap("I", 0),
        (i, j) -> 0
      ),
      binOp -> mock(Semigroup.class)
    );
  }
  
  @Test
  void invalidIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
  
    RuntimeException e = assertThrows(RuntimeException.class, () -> ValidatedMonoid.createMonoid(
      "L1",
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> product[i][j]
      ),
      binOp -> mock(Semigroup.class)
    ));
    
    assertEquals(
      "Monoids may only be created with valid identity elements.", e.getMessage()
    );
  }
}