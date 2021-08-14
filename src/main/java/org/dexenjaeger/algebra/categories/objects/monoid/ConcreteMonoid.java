package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Getter;
import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperator;

import java.util.Map;

public class ConcreteMonoid extends BaseBinaryOperator implements Monoid {
  @Getter
  private final int identity;
  
  ConcreteMonoid(
    String operatorSymbol,
    int size,
    String[] elements,
    int identity,
    Map<String, Integer> lookup,
    int[][] multiplicationTable
  ) {
    super(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
    this.identity = identity;
  }
  
  @Override
  public String getIdentityDisplay() {
    return elements[identity];
  }
  
  public static MonoidBuilder builder() {
    return new MonoidBuilder();
  }
}
