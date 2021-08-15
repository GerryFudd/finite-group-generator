package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonoidServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final MonoidService monoidService = injector.getInstance(MonoidService.class);
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  @Test
  void invalidIdentity() {
    int[][] product = {
      {1, 0, 3, 2},
      {0, 1, 2, 3},
      {1, 0, 3, 2},
      {0, 1, 2, 3}
    };
    
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> monoidService.createMonoid(
        summary.getLookupMap().get("L1"),
        summary.getElements(),
        summary.getOperator()
      ));
    
    assertEquals(
      "The element L1 is not an identity in this Monoid\n\n" +
        "_*__|_L1_L2_a__b__\n" +
        " L1 | L1 L2 a  b  \n" +
        " L2 | L1 L2 a  b  \n" +
        " a  | a  b  L1 L2 \n" +
        " b  | a  b  L1 L2 \n", e.getMessage()
    );
  }
}