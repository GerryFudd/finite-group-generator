package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class ConcreteBinaryOperator implements BinaryOperator {
  @Getter
  @Builder.Default
  private final String operatorSymbol = "*";
  @Getter
  private final Set<String> elements;
  private final BiFunction<String, String, String> operator;
  
  @Override
  public String prod(String a, String b) {
    return operator.apply(a, b);
  }
}
