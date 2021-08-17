package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    
    assertTrue(
      Pattern.matches("Binary operator is not associative \\(\\w, \\w, \\w\\)\\n\\n" +
        "_\\*_\\|_x_y_z_\\n" +
        " x \\| x y z \\n" +
        " y \\| x x x \\n" +
        " z \\| x y z \\n", e.getMessage()),
      String.format("Expected exception to match pattern, instead got\n%s", e.getMessage())
    );
  }
  
  
}