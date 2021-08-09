package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.ConcreteBinaryOperator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryOperatorValidatorTest {
  private final Validator<BinaryOperator> binaryOperatorValidator = Guice.createInjector(new AlgebraModule()).getInstance(Key.get(new TypeLiteral<Validator<BinaryOperator>>() {
  }));
  
  @Test
  void validateBinaryOperator() {
    BinaryOperator binaryOperator = ConcreteBinaryOperator.builder()
      .elements(Set.of("a"))
      .operator((a, b) -> "b")
      .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> binaryOperatorValidator.validate(binaryOperator));
    
    assertEquals(
      "This set is not closed under *\n\n" +
        "_*____|_a____\n" +
        " a    | b    \n", e.getMessage()
    );
  }
}