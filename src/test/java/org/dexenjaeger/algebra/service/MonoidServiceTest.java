package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonoidServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final MonoidService monoidService = injector.getInstance(MonoidService.class);
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  @Test
  void invalidIdentity() {
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      List.of(
        new Mapping(new int[]{1, 0, 0}),
        new Mapping(new int[]{0, 1, 1}),
        new Mapping(new int[]{1, 0, 1}),
        new Mapping(new int[]{0, 1, 0})
      )
    );
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> monoidService.createMonoid(
        0,
        summary.getElements(),
        summary.getOperator()
      ));
    
    assertEquals(
      "The element L is not an identity in this Monoid\n\n" +
        "_*__|_L__L2_a__b__\n" +
        " L  | L  L2 a  b  \n" +
        " L2 | L  L2 a  b  \n" +
        " a  | a  b  L  L2 \n" +
        " b  | a  b  L  L2 \n", e.getMessage()
    );
  }
}