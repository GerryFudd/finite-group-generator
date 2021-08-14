package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryOperatorValidatorTest {
  @SuppressWarnings("Convert2Diamond") // The diamond doesn't work here
  private final Validator<BinaryOperator> binaryOperatorValidator = Guice.createInjector(new AlgebraModule()).getInstance(Key.get(new TypeLiteral<Validator<BinaryOperator>>() {
  }));
  
  @Test
  void validateBinaryOperator() {
    String[] elements = {"a"};
    BinaryOperator binaryOperator = BinaryOperator.builder()
      .elements(elements)
      .lookup(Map.of("a", 0))
      .operator((a, b) -> 1)
      .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> binaryOperatorValidator.validate(binaryOperator));
    
    assertEquals(
      "The product of a and a doesn't exist in this binary operator\n\n" +
        "_*____|_a____\n" +
        " a    | [1?] \n", e.getMessage()
    );
  }
}