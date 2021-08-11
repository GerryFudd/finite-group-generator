package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.cycle.Cycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BinaryOperatorServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  
  private String getElements(BinaryOperatorSummary summary) {
    return String.join(", ", BinaryOperatorUtil.getSortedElements(summary.getBinaryOperator().getElementsDisplay(), summary.getIdentityDisplay()));
  }
  
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withLeftIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
    
    BinaryOperatorSummary result = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    assertEquals(
      "L1, L2, a, b",
      getElements(result)
    );
    
    assertNull(result.getIdentityDisplay());
    assertNull(result.getDisplayInversesMap());
  }
  
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withRightIdentity() {
    int[][] product = {
      {1, 0, 1, 0},
      {0, 1, 0, 1},
      {3, 2, 3, 2},
      {2, 3, 2, 3}
    };
    
    BinaryOperatorSummary result = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    assertEquals(
      "R1, R2, a, b",
      getElements(result)
    );
    
    assertNull(result.getIdentityDisplay());
    assertNull(result.getDisplayInversesMap());
  }
  
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withIdentityAndInerses() {
    int[][] product = {
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {2, 3, 0, 1},
      {3, 2, 1, 0}
    };
    
    BinaryOperatorSummary result = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    assertEquals(
      "I, a, b, c",
      getElements(result)
    );
    
    assertEquals("I", result.getIdentityDisplay());
    assertEquals(
      Map.of("I", "I", "a", "a", "b", "b", "c", "c"),
      result.getDisplayInversesMap()
    );
  }
  
  @Test
  void validInverses() {
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> (i + j) % 4
    );
    
    Group result = ConcreteGroup.builder()
                     .inversesMap(summary.getInversesMap())
                     .cyclesMap(summary.getCyclesMap())
                     .maximalCycles(Set.of(
                       Cycle.builder()
                         .elements(List.of("a", "b", "c", "I"))
                         .build()))
                     .lookup(summary.getLookupMap())
                     .operator(summary.getBinaryOperator()::prod)
                     .build();
    
    result.getElementsDisplay().forEach(element -> assertEquals(
      result.getIdentityDisplay(),
      result.prod(
        element,
        result.getInverse(element)
      )
    ));
    
    for (int i = 0; i < result.getSize(); i++) {
      assertEquals(
        result.getIdentity(),
        result.prod(
          i,
          result.getInverse(i)
        )
      );
    }
  }
}