package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.Set;

public interface BinaryOperator {
  String getOperatorSymbol();
  Set<String> getElements();
  String prod(String a, String b);
  default String getMultiplicationTable() {
    return BinaryOperatorUtil.getMultiplicationTable(this);
  }
}
