package org.dexenjaeger.algebra.categories.objects.semigroup;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class ConcreteSemigroup implements Semigroup {
  @Getter
  @Builder.Default
  private final String operatorSymbol = "*";
  @Getter
  private final Set<String> elementsDisplay;
  private final BiFunction<String, String, String> operator;
  
  @Override
  public int getSize() {
    return elementsDisplay.size();
  }
  
  @Override
  public String prod(String a, String b) {
    return operator.apply(a, b);
  }
  
  @Override
  public int prod(int a, int b) {
    return 0;
  }
}
