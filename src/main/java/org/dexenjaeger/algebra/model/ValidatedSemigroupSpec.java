package org.dexenjaeger.algebra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

@Getter
@AllArgsConstructor
public class ValidatedSemigroupSpec {
  private final String operatorSymbol;
  private final ValidatingBinaryOperator binaryOperator;
  
  public ValidatedSemigroupSpec(ValidatingBinaryOperator binaryOperator) {
    this("*", binaryOperator);
  }
}
