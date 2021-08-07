package org.dexenjaeger.algebra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidatedGroupSpec {
  private final String identity;
  private final Map<String, String> inversesMap;
  private final ValidatingBinaryOperator binaryOperator;
  
  public ValidatedGroupSpec(
    Map<String, String> inversesMap,
    ValidatingBinaryOperator binaryOperator
  ) {
    this("I", inversesMap, binaryOperator);
  }
}
