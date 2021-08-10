package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.monoid.ConcreteMonoid;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonoidValidatorTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  @SuppressWarnings("Convert2Diamond") // The diamond doesn't work here
  private final Validator<Monoid> validator = injector.getInstance(Key.get(new TypeLiteral<Validator<Monoid>>() {
  }));
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  @Test
  void invalidIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
    
    BinaryOperator binOp = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    ).getBinaryOperator();
    
    Monoid monoid = ConcreteMonoid.builder()
                      .identityDisplay("L1")
                      .elementsDisplay(binOp.getElementsDisplay())
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