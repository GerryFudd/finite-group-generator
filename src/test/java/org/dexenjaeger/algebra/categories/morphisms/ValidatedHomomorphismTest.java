package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.categories.objects.group.ValidatedGroup;
import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
import org.dexenjaeger.algebra.model.ValidatingBinaryOperator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatedHomomorphismTest {
  @Test
  void createHomomorphismTest() {
    ValidatedHomomorphism validatedHomomorphism = ValidatedHomomorphism.createHomomorphism(
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
      List.of("E"), validatedHomomorphism.getRange().getElementsAsList()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidHomomorphismFunction() {
    String[] elements = {"I", "a", "b"};
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> ValidatedHomomorphism.createHomomorphism(
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .inversesMap(Map.of("I", "I", "a", "b", "b", "a"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("I")),
              3, Set.of(List.of("a", "b", "I"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              elements,
              Map.of("I", 0, "a", 1, "b", 2),
              (a, b) -> (a + b) % 3
            ))
            .build()
        ),
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
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    String[] rangeElements = {"E", "C"};
    Map<String, Integer> rangeLookup = Map.of("E", 0, "C", 1);
    
    String[] kernelElements = {"I", "d", "e"};
    Map<String, Integer> kernelLookup = Map.of("I", 0, "d", 1, "e", 2);
    
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> ValidatedHomomorphism.createHomomorphism(
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .inversesMap(Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("I")),
              6, Set.of(List.of("a", "b", "c", "d", "e", "I"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              elements, lookup, (a, b) -> (a + b) % 6
            ))
            .build()
        ),
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .identity("E")
            .inversesMap(Map.of("E", "E", "C", "C"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("E")),
              2, Set.of(List.of("C", "E"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              rangeElements, rangeLookup, (a, b) -> (a + b) % 2
            ))
            .build()
        ),
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .inversesMap(Map.of("I", "I", "d", "e", "e", "d"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("I")),
              3, Set.of(List.of("d", "e", "I"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              kernelElements, kernelLookup,
              (a, b) -> (a + b) % 3
            ))
            .build()
        ),
        (a) -> rangeElements[lookup.get(a) % 2]
      )
    );
    
    assertEquals(
      "Subset is not the inverse image of the identity.", e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernelOperation() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    int[][] kernelMult = {
      {0, 1, 2, 3, 4, 5},
      {1, 0, 5, 4, 3, 2},
      {2, 4, 0, 5, 1, 3},
      {3, 5, 4, 0, 2, 1},
      {4, 2, 3, 1, 5, 0},
      {5, 3, 1, 2, 0, 4}
    };
    
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> ValidatedHomomorphism.createHomomorphism(
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .inversesMap(Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("I")),
              6, Set.of(List.of("a", "b", "c", "d", "e", "I"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              elements, lookup, (a, b) -> (a + b) % 6
            ))
            .build()
        ),
        new TrivialGroup("E"),
        ValidatedGroup.createGroup(
          ValidatedGroupSpec.builder()
            .inversesMap(Map.of("I", "I", "a", "a", "b", "b", "c", "c", "d", "e", "e", "d"))
            .cyclesMap(Map.of(
              1, Set.of(List.of("I")),
              2, Set.of(List.of("a", "I"), List.of("b", "I"), List.of("c", "I")),
              3, Set.of(List.of("d", "e", "I"))
            ))
            .binaryOperator(new ValidatingBinaryOperator(
              elements, lookup, (a, b) -> kernelMult[a][b]
            ))
            .build()
        ),
        (a) -> "E"
      )
    );
    
    assertEquals(
      "Subset is not a subgroup.", e.getMessage()
    );
  }
}