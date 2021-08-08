package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class HomomorphismUtilTest {
  @Test
  void createHomomorphismTest() throws ValidationException {
    Homomorphism validatedHomomorphism = HomomorphismUtil.createHomomorphism(
      new TrivialGroup(),
      a -> "E"
    );
    
    assertEquals(
      "I", validatedHomomorphism.getDomain().getIdentity()
    );
    
    assertEquals(
      validatedHomomorphism.getDomain().getMultiplicationTable(),
      validatedHomomorphism.getKernel().getMultiplicationTable()
    );
    
    assertEquals(
      Set.of("E"), validatedHomomorphism.getRange().getElements()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidHomomorphismFunction() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> HomomorphismUtil.createHomomorphism(
        BinaryOperatorUtil.getCyclicGroup("I", "a", "b"),
        (a) -> "b".equals(a) ? "B" : "E"
      )
    );
    
    assertEquals(
      "Invalid homomorphism. Kernel is not a subgroup since a is in the kernel but its inverse b is not.",
      e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernel() {
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    String[] rangeElements = {"E", "C"};
    
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> HomomorphismUtil.createHomomorphism(
        BinaryOperatorUtil.getCyclicGroup(
          "I", "a", "b", "c", "d", "e"
        ),
        BinaryOperatorUtil.getCyclicGroup(
          "E", "C"
        ),
        BinaryOperatorUtil.getCyclicGroup(
          "I", "d", "e"
        ),
        (a) -> rangeElements[lookup.get(a) % 2]
      )
    );
    
    assertEquals(
      "Kernel is not the inverse image of the identity.", e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernelOperation() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    
    int[][] kernelMult = {
      {0, 1, 2, 3, 4, 5},
      {1, 0, 5, 4, 3, 2},
      {2, 4, 0, 5, 1, 3},
      {3, 5, 4, 0, 2, 1},
      {4, 2, 3, 1, 5, 0},
      {5, 3, 1, 2, 0, 4}
    };
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> HomomorphismUtil.createHomomorphism(
        BinaryOperatorUtil.getCyclicGroup(elements),
        new TrivialGroup("E"),
        ConcreteGroup.builder()
          .inversesMap(Map.of("I", "I", "a", "a", "b", "b", "c", "c", "d", "e", "e", "d"))
          .cyclesMap(Map.of(
            1, Set.of(List.of("I")),
            2, Set.of(List.of("a", "I"), List.of("b", "I"), List.of("c", "I")),
            3, Set.of(List.of("d", "e", "I"))
          ))
          .elements(Set.of(elements))
          .operator(BinaryOperatorUtil.createOperator(
            elements, (a, b) -> kernelMult[a][b]
          ))
          .build(),
        (a) -> "E"
      )
    );
    
    assertEquals(
      "Kernel is not a subgroup.", e.getMessage()
    );
  }
  
  @Test
  void trivialHomomorphismCreatesKernelThatEqualsGroup() throws ValidationException {
    Group group = BinaryOperatorUtil.getCyclicGroup(
      "I", "a", "b"
    );
    Homomorphism result = HomomorphismUtil.createHomomorphism(
      group, (a) -> "E"
    );
    
    assertEquals(
      group.getMultiplicationTable(),
      result.getKernel().getMultiplicationTable()
    );
  }
}