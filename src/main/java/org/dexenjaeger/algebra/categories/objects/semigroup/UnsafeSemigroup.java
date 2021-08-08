package org.dexenjaeger.algebra.categories.objects.semigroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class UnsafeSemigroup implements Semigroup {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final Set<String> elements;
  private final BiFunction<String, String, String> operator;
  
  @Override
  public String prod(String a, String b) {
    return operator.apply(a, b);
  }
}
