package org.dexenjaeger.algebra.categories.objects;

import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcreteGroupTest {
  @Test
  void cycleMapTest() {
    String[] elements = {"I", "a"};
    Group group = ConcreteGroup.builder()
                    .inversesMap(Map.of("I", "I", "a", "a"))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      2, Set.of(List.of("a", "I"))
                    ))
                    .elements(Set.of(elements))
                    .operator(BinaryOperatorUtil.createOperator(
                      elements, (i, j) -> (i + j) % 2
                    ))
                    .build();
    
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
  void validInverses() {
    BinaryOperatorSummary summary = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> (i + j) % 4
    );
    
    ConcreteGroup result = ConcreteGroup.builder()
                             .inversesMap(summary.getInverseMap())
                             .cyclesMap(summary.getCyclesMap())
                             .elements(summary.getBinaryOperator().getElements())
                             .operator(summary.getBinaryOperator()::prod)
                             .build();
    
    result.getElements().forEach(element -> assertEquals(
      result.getIdentity(),
      result.prod(
        element,
        result.getInverse(element)
      )
    ));
  }
}