package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Getter;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;

import java.util.Map;

public class ConcreteMonoid extends BaseBinaryOperator implements Monoid {
  @Getter
  private final int identity;
  
  ConcreteMonoid(
    OperatorSymbol operatorSymbol,
    int size,
    Element[] elements,
    int identity,
    Map<Element, Integer> lookup,
    int[][] multiplicationTable
  ) {
    super(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
    this.identity = identity;
  }
  
  @Override
  public Element getIdentityDisplay() {
    return elements[identity];
  }
}
