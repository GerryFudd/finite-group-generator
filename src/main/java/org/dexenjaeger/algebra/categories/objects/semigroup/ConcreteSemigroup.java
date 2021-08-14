package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperator;

import java.util.Map;

public class ConcreteSemigroup extends BaseBinaryOperator implements Semigroup {
  
  ConcreteSemigroup(
    String operatorSymbol,
    int size,
    String[] elements,
    Map<String, Integer> lookup,
    int[][] multiplicationTable
  ) {
    super(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
  }
  
  public static SemigroupBuilder builder() {
    return new SemigroupBuilder();
  }
}
