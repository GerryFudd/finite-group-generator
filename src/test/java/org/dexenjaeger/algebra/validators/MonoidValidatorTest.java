package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.monoid.ConcreteMonoid;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonoidValidatorTest {
  MonoidValidator validator = new MonoidValidator(new SemigroupValidator(new BinaryOperatorValidator()));
  @Test
  void invalidIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
    
    BinaryOperator binOp = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    ).getBinaryOperator();
    
    Monoid monoid = ConcreteMonoid.builder()
                      .identity("L1")
                      .elements(binOp.getElements())
                      .operator(binOp::prod)
                      .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> validator.validate(monoid));
    
    assertEquals(
      "The element L1 is not an identity in this Monoid\n\n" +
        "_*____|_L1___L2___a____b____\n" +
        " L1   | L1   L2   a    b    \n" +
        " L2   | L1   L2   a    b    \n" +
        " a    | a    b    L1   L2   \n" +
        " b    | a    b    L1   L2   \n", e.getMessage()
    );
  }
}