package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.TrivialGroup;
import org.dexenjaeger.algebra.categories.objects.UnsafeGroup;
import org.dexenjaeger.algebra.categories.objects.UnsafeMonoid;
import org.dexenjaeger.algebra.categories.objects.UnsafeSemigroup;
import org.dexenjaeger.algebra.categories.objects.ValidatedGroup;
import org.dexenjaeger.algebra.model.ValidatedGroupSpec;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatedHomomorphismTest {
  @Test
  void createHomomorphismTest() {
    String[] elements = {"I"};
    ValidatedHomomorphism validatedHomomorphism = ValidatedHomomorphism.createHomomorphism(
      new ValidatedGroupSpec(
        Collections.singletonMap("I" ,"I"),
        new ValidatingBinaryOperator(
          elements, Collections.singletonMap("I", 0), (a, b) -> 0
        )
      ),
      ValidatedGroup::createGroup,
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
        new ValidatedGroupSpec(
          Map.of("I", "I", "a", "b", "b", "a"),
          new ValidatingBinaryOperator(
            elements,
            Map.of("I", 0, "a", 1, "b", 2),
            (a, b) -> (a + b) % 3
          )
        ),
        ValidatedGroup::createGroup,
        (a) -> "b".equals(a) ? "B" : "E"
      )
    );
    
    assertEquals(
      "A Homomorphism must be constructed from a valid homomorphism function.", e.getMessage()
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
          new ValidatedGroupSpec(
            Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"),
            new ValidatingBinaryOperator(
              elements, lookup, (a, b) -> (a + b) % 6
            )
          )
        ),
        ValidatedGroup.createGroup(
          new ValidatedGroupSpec(
            "E",
            Map.of("E", "E", "C", "C"),
            new ValidatingBinaryOperator(
              rangeElements, rangeLookup, (a, b) -> (a + b) % 2
            )
          )
        ),
        ValidatedGroup.createGroup(
          new ValidatedGroupSpec(
          Map.of("I", "I", "d", "e", "e", "d"),
          new ValidatingBinaryOperator(
            kernelElements, kernelLookup,
              (a, b) -> (a + b) % 3
            )
          )
        ),
        (a) -> rangeElements[lookup.get(a) % 2]
      )
    );
    
    assertEquals(
      "A Homomorphism's kernel must be the inverse image of the range's identity.", e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernelOperation() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    String[] rangeElements = {"E"};
    Map<String, Integer> rangeLookup = Map.of("E", 0);
    
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
          new ValidatedGroupSpec(
            Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"),
            new ValidatingBinaryOperator(
              elements, lookup, (a, b) -> (a + b) % 6
            )
          )
        ),
        new TrivialGroup("E"),
        ValidatedGroup.createGroup(
          new ValidatedGroupSpec(
          Map.of("I", "I", "a", "a", "b", "b", "c", "c", "d", "e", "e", "d"),
          new ValidatingBinaryOperator(
            elements, lookup, (a, b) -> kernelMult[a][b]
          )
          )
        ),
        (a) -> "E"
      )
    );
    
    assertEquals(
      "A Homomorphism's kernel must be a subgroup of the domain.", e.getMessage()
    );
  }
}