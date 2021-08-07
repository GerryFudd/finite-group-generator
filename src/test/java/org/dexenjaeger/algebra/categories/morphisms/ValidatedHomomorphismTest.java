package org.dexenjaeger.algebra.categories.morphisms;

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
        new UnsafeGroup(
          Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"),
          new UnsafeMonoid(
            "I",
            new UnsafeSemigroup(
              "*", List.of(elements),
              (a, b) -> elements[(lookup.get(a) + lookup.get(b)) % 6]
            )
          )
        ),
        new UnsafeGroup(
          Map.of("E", "E", "C", "C"),
          new UnsafeMonoid(
            "E",
            new UnsafeSemigroup(
              "x", List.of(rangeElements),
              (a, b) -> rangeElements[(rangeLookup.get(a) + rangeLookup.get(b)) % 2]
            )
          )
        ),
        new UnsafeGroup(
          Map.of("I", "I", "d", "e", "e", "d"),
          new UnsafeMonoid(
            "I",
            new UnsafeSemigroup(
              "*", List.of(kernelElements),
              (a, b) -> kernelElements[(kernelLookup.get(a) + kernelLookup.get(b)) % 3]
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
    
    String[][] kernelMult = {
      {"I", "a", "b", "c", "d", "e"},
      {"a", "I", "e", "d", "c", "b"},
      {"b", "d", "I", "e", "a", "c"},
      {"c", "e", "d", "I", "b", "a"},
      {"d", "b", "c", "a", "e", "I"},
      {"e", "c", "a", "b", "I", "d"}
    };
  
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> ValidatedHomomorphism.createHomomorphism(
        new UnsafeGroup(
          Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"),
          new UnsafeMonoid(
            "I",
            new UnsafeSemigroup(
              "*", List.of(elements),
              (a, b) -> elements[(lookup.get(a) + lookup.get(b)) % 6]
            )
          )
        ),
        new UnsafeGroup(
          Map.of("E", "E"),
          new UnsafeMonoid(
            "E",
            new UnsafeSemigroup(
              "x", List.of(rangeElements),
              (a, b) -> "E"
            )
          )
        ),
        new UnsafeGroup(
          Map.of("I", "I", "a", "a", "b", "b", "c", "c", "d", "e"),
          new UnsafeMonoid(
            "I",
            new UnsafeSemigroup(
              "*", List.of(elements),
              (a, b) -> kernelMult[lookup.get(a)][lookup.get(b)]
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