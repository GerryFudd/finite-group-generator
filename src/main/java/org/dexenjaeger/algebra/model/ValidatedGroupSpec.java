package org.dexenjaeger.algebra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidatedGroupSpec {
  private final String operatorSymbol;
  private final String identity;
  private final Map<String, String> inversesMap;
  private final ValidatingBinaryOperator binaryOperator;
  
  public ValidatedGroupSpec(
    Map<String, String> inversesMap,
    ValidatingBinaryOperator binaryOperator
  ) {
    this("I", inversesMap, binaryOperator);
  }
  
  public ValidatedGroupSpec(
    String identity,
    Map<String, String> inversesMap,
    ValidatingBinaryOperator binaryOperator
  ) {
    this("*", identity, inversesMap, binaryOperator);
  }
}
