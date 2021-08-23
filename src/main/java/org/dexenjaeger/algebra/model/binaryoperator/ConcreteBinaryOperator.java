package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.model.Element;

import java.util.Map;

public class ConcreteBinaryOperator extends BaseBinaryOperator {
  ConcreteBinaryOperator(
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
