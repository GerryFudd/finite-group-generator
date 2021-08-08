package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.categories.objects.group.SafeGroup;
import org.dexenjaeger.algebra.categories.objects.group.ValidatedGroup;
import org.dexenjaeger.algebra.categories.objects.monoid.ValidatedMonoid;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
import org.dexenjaeger.algebra.model.ValidatingBinaryOperator;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.MoreArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ValidatedGroupTest {
  @Test
  void staticInitializerTest() {
    SafeGroup group = ValidatedGroup.createGroup(
      ValidatedGroupSpec.builder()
        .inversesMap(Map.of("I", "I", "a", "a"))
        .cyclesMap(Map.of(
          1, Set.of(List.of("I")),
          2, Set.of(List.of("a", "I"))
        ))
        .binaryOperator(new ValidatingBinaryOperator(
          MoreArrayUtils.createArray("I", "a"),
          Map.of("I", 0, "a", 1),
          (i, j) -> (i + j) % 2
        ))
        .build(),
      (id, binOp) -> mock(ValidatedMonoid.class)
    );
    
    assertEquals(
      Set.of(List.of("I")),
      group.getNCycles(1)
    );
    
    assertEquals(
      Set.of(List.of("a", "I")),
      group.getNCycles(2)
    );
    
    assertEquals(
      Set.of(),
      group.getNCycles(3)
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
      ValidatedGroupSpec.builder()
        .inversesMap(Map.of("I", "I", "a", "b", "b", "a", "c", "c"))
        .binaryOperator(summary.getBinaryOperator())
        .build(),
      (id, binOp) -> mock(ValidatedMonoid.class)
    ));
    
    assertEquals(
      "Inverse map is not valid.", e.getMessage()
    );
  }
  
  @Test
  void validInverses() {
    BinaryOperatorSummary summary = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> (i + j) % 4
    );
    
    ValidatedGroup result = ValidatedGroup.createGroup(
      ValidatedGroupSpec.builder()
        .inversesMap(summary.getInverseMap())
        .cyclesMap(summary.getCyclesMap())
        .binaryOperator(summary.getBinaryOperator())
        .build()
    );
    
    result.getElements().forEach(element -> assertEquals(
      result.getIdentity(),
      result.prod(
        element,
        result.getInverse(element)
      )
    ));
  }
}