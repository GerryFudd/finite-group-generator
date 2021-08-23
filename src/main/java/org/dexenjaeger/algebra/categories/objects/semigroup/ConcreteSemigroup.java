package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;

import java.util.Map;

public class ConcreteSemigroup extends BaseBinaryOperator implements Semigroup {
  
  ConcreteSemigroup(
    OperatorSymbol operatorSymbol,
    int size,
    Element[] elements,
    Map<Element, Integer> lookup,
    int[][] multiplicationTable
  ) {
    super(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
  }
}
