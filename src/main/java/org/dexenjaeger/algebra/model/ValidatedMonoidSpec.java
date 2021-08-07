package org.dexenjaeger.algebra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

@Getter
@AllArgsConstructor
public class ValidatedMonoidSpec {
  private final String operatorSymbol;
  private final String identity;
  private final ValidatingBinaryOperator binaryOperator;
  
  public ValidatedMonoidSpec(String identity, ValidatingBinaryOperator binaryOperator) {
    this("*", identity, binaryOperator);
  }
}
