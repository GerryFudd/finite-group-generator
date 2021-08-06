package org.dexenjaeger.algebra.model;

import java.util.List;

public interface AlgebraicStructure {
  List<String> getElementsAsList();
  String getProduct(String a, String b);
  String getMultiplicationTable();
}
