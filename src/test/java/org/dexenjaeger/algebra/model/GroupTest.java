package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GroupTest {
  @Test
  void staticInitializerTest() {
    Group.createGroup(
      Collections.singletonMap("I", "I"),
      new BinaryOperator(
        MoreArrayUtils.createArray("I"),
        Collections.singletonMap("I", 0),
        (i, j) -> 0
      ),
      binOp -> mock(AlgebraicStructureWithIdentity.class)
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
    
    RuntimeException e = assertThrows(RuntimeException.class, () -> Group.createGroup(
      Map.of("I", "I", "a", "b", "b", "a", "c", "c"),
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> product[i][j]
      ),
      binOp -> mock(AlgebraicStructureWithIdentity.class)
    ));
    
    assertEquals(
      "Monoids may only be created with valid inverses for all elements.", e.getMessage()
    );
  }
  
  @Test
  void validInverses() {
    Group result = Group.createGroup(
      Map.of("I", "I", "a", "a3", "a2", "a2", "a3", "a"),
      BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
        4,
        (i, j) -> (i + j) % 4
      ),
      binOp -> Monoid.createMonoid(
        "I",
        binOp,
        bop -> Semigroup.createSemigroup("*", bop)
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