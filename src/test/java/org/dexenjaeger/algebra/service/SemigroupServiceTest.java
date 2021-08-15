package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SemigroupServiceTest {
  public final Injector injector = Guice.createInjector(new AlgebraModule());
  public final SemigroupService semigroupService = injector.getInstance(
    SemigroupService.class
  );
  
  private int nonAssociative(int a, int b) {
    if (a == 1) {
      return 0;
    }
    return b;
  }
  
  @Test
  void invalidBinaryOperator() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> semigroupService.createSemigroup(
        new String[]{"x", "y", "z"},
        this::nonAssociative
      ));
    
    assertEquals(
      "Binary operator is not associative\n\n" +
        "_*_|_x_y_z_\n" +
        " x | x y z \n" +
        " y | x x x \n" +
        " z | x y z \n", e.getMessage()
    );
  }
}