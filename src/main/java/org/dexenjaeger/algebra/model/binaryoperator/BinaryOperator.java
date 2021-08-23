package org.dexenjaeger.algebra.model.binaryoperator;

import org.dexenjaeger.algebra.model.Element;

import java.util.List;
import java.util.Set;

public interface BinaryOperator {
  OperatorSymbol getOperatorSymbol();
  Set<Element> getElementsDisplay();
  int getSize();
  Element prod(Element a, Element b);
  int prod(int a, int b);
  Integer eval(Element a);
  Element display(int i);
  String printMultiplicationTable();
  List<Element> getSortedElements();
  
  static BinaryOperatorBuilder builder() {
    return new BinaryOperatorBuilder();
  }
  
}
