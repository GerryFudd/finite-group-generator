package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.BinaryOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOperatorUtilTest {
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withLeftIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
    
    BinaryOperator result = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    assertEquals(
      "L1, L2, a, b",
      String.join(", ", result.getElements())
    );
  }
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withRightIdentity() {
    int[][] product = {
      {1, 0, 1, 0},
      {0, 1, 0, 1},
      {3, 2, 3, 2},
      {2, 3, 2, 3}
    };
    
    BinaryOperator result = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    assertEquals(
      "R1, R2, a, b",
      String.join(", ", result.getElements())
    );
  }
}