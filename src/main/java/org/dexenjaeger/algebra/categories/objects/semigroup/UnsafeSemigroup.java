package org.dexenjaeger.algebra.categories.objects.semigroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class UnsafeSemigroup implements Semigroup {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final List<String> elementsAsList;
  private final BiFunction<String, String, String> binaryOperator;
  
  @Override
  public String getProduct(String a, String b) {
    return binaryOperator.apply(a, b);
  }
}
