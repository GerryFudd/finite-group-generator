package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class ConcreteMonoid implements Monoid {
  @Getter
  @Builder.Default
  private final String identity = "I";
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
