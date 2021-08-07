package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
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
      new ValidatedGroupSpec(
        Collections.singletonMap("I", "I"),
        new ValidatingBinaryOperator(
          MoreArrayUtils.createArray("I"),
          Collections.singletonMap("I", 0),
          (i, j) -> 0
        )
      ),
      (id, binOp) -> mock(ValidatedMonoid.class)
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
  
    BinaryOperatorSummary summary = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    RuntimeException e = assertThrows(RuntimeException.class, () -> ValidatedGroup.createGroup(
      new ValidatedGroupSpec(
        Map.of("I", "I", "a", "b", "b", "a", "c", "c"),
        summary.getBinaryOperator()
      ),
      (id, binOp) -> mock(ValidatedMonoid.class)
    ));
    
    assertEquals(
      "Groups may only be created with valid inverses for all elements.", e.getMessage()
    );
  }
  
  @Test
  void validInverses() {
    BinaryOperatorSummary summary = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> (i + j) % 4
    );
    
    ValidatedGroup result = ValidatedGroup.createGroup(
      new ValidatedGroupSpec(
        summary.getInverseMap(),
        summary.getBinaryOperator()
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