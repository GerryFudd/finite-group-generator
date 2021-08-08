package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.semigroup.ConcreteSemigroup;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SemigroupValidatorTest {
  private final SemigroupValidator validator = new SemigroupValidator(
    new BinaryOperatorValidator()
  );
  
  private final Set<String> elements = Set.of("x", "y", "z");
  
  private String nonAssociative(String a, String b) {
    if (a.equals("y")) {
      return "x";
    }
    return b;
  }
  @Test
  void invalidBinaryOperator() {
    Semigroup semigroup = ConcreteSemigroup.builder()
      .elements(elements)
      .operator(this::nonAssociative)
      .build();
    ValidationException e = assertThrows(ValidationException.class, () -> validator.validate(semigroup));
    
    assertEquals(
      "Binary operator is not associative\n\n" +
        "_*____|_x____y____z____\n" +
        " x    | x    y    z    \n" +
        " y    | x    x    x    \n" +
        " z    | x    y    z    \n", e.getMessage()
    );
  }
}