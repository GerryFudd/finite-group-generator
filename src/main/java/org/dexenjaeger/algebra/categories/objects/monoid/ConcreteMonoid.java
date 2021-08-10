package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class ConcreteMonoid implements Monoid {
  @Getter
  @Builder.Default
  private final String identityDisplay = "I";
  @Getter
  @Builder.Default
  private final String operatorSymbol = "*";
  @Getter
  private final Set<String> elementsDisplay;
  private final BiFunction<String, String, String> operator;
  
  @Override
  public int getSize() {
    return 0;
  }
  
  @Override
  public String prod(String a, String b) {
    return operator.apply(a, b);
  }
  
  @Override
  public int prod(int a, int b) {
    return 0;
  }
  
  @Override
  public int getIdentity() {
    return 0;
  }
}
