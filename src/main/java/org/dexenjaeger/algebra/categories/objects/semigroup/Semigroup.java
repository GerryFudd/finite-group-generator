package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.List;

public interface Semigroup {
  String getOperatorSymbol();
  List<String> getElementsAsList();
  String getProduct(String a, String b);
  default String getMultiplicationTable() {
    return BinaryOperatorUtil.getMultiplicationTable(
      getOperatorSymbol(),
      getElementsAsList(),
      this::getProduct
    );
  }
  default List<String> getCyclicGroup(String element) {
    return BinaryOperatorUtil.getCyclicGroup(element, this::getProduct);
  }
}
