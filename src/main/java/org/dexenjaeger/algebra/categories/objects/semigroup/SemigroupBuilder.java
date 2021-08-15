package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperatorBuilder;

public class SemigroupBuilder extends BaseBinaryOperatorBuilder<Semigroup> {
  
  @Override
  public Semigroup build() {
    return new ConcreteSemigroup(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
  }
}
