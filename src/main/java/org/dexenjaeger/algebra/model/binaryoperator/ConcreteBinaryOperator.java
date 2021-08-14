package org.dexenjaeger.algebra.model.binaryoperator;

import java.util.Map;

public class ConcreteBinaryOperator extends BaseBinaryOperator {
  ConcreteBinaryOperator(
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
}
