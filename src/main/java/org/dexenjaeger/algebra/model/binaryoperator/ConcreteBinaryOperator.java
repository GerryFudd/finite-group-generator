package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class ConcreteBinaryOperator implements BinaryOperator {
  @Getter
  @Builder.Default
  private final String operatorSymbol = "*";
  @Getter
  private final int size;
  private final String[] elements;
  private final BiFunction<Integer, Integer, Integer> operator;
  private final Map<String, Integer> lookup;
  
  @Override
  public Set<String> getElementsDisplay() {
    return Set.of(elements);
  }
  
  @Override
  public String prod(String a, String b) {
    return elements[operator.apply(lookup.get(a), lookup.get(b))];
  }
  
  @Override
  public int prod(int a, int b) {
    return operator.apply(a, b);
  }
  
  @Override
  public Integer eval(String a) {
    return lookup.get(a);
  }
}
