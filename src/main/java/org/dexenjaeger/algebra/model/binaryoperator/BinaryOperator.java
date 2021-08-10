package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.Set;

public interface BinaryOperator {
  String getOperatorSymbol();
  Set<String> getElementsDisplay();
  int getSize();
  String prod(String a, String b);
  int prod(int a, int b);
  default String printMultiplicationTable() {
    return BinaryOperatorUtil.printMultiplicationTable(this);
  }
}
