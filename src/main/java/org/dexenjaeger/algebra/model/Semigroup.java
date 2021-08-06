package org.dexenjaeger.algebra.model;

import java.util.List;

public interface Semigroup {
  List<String> getElementsAsList();
  String getProduct(String a, String b);
  String getMultiplicationTable();
}
