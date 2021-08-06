package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MonoidTest {
  @Test
  void staticInitializerTest() {
    Monoid.createMonoid(
      "I",
      new BinaryOperator(
        MoreArrayUtils.createArray("I"),
        Collections.singletonMap("I", 0),
        (i, j) -> 0
      ),
      binOp -> mock(AlgebraicStructure.class)
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
  
    RuntimeException e = assertThrows(RuntimeException.class, () -> Monoid.createMonoid(
      "L1",
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> product[i][j]
      ),
      binOp -> mock(AlgebraicStructure.class)
    ));
    
    assertEquals(
      "Monoids may only be created with valid identity elements.", e.getMessage()
    );
  }
}