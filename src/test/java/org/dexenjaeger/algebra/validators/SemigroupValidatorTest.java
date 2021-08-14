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
  
  private final Set<String> elements = Set.of();
  
  private int nonAssociative(int a, int b) {
    if (a == 1) {
      return 0;
    }
    return b;
  }
  @Test
  void invalidBinaryOperator() {
    Semigroup semigroup = ConcreteSemigroup.builder()
      .elements("x", "y", "z")
      .operator(this::nonAssociative)
      .build();
    ValidationException e = assertThrows(ValidationException.class, () -> validator.validate(semigroup));
    
    assertEquals(
      "Binary operator is not associative\n\n" +
        "_*_|_x_y_z_\n" +
        " x | x y z \n" +
        " y | x x x \n" +
        " z | x y z \n", e.getMessage()
    );
  }
}