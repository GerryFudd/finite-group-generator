package org.dexenjaeger.algebra.model.binaryoperator;

import java.util.Set;

public interface BinaryOperator {
  String getOperatorSymbol();
  Set<String> getElementsDisplay();
  int getSize();
  String prod(String a, String b);
  int prod(int a, int b);
  Integer eval(String a);
  String display(int i);
  String printMultiplicationTable();
  
  static BinaryOperatorBuilder builder() {
    return new BinaryOperatorBuilder();
  }
}
