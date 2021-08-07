package org.dexenjaeger.algebra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidatedMonoidSpec {
  private final String identity;
  private final ValidatingBinaryOperator binaryOperator;
}
