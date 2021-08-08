package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.BinaryOperator;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.List;

public interface Semigroup extends BinaryOperator {
  default List<String> getCyclicGroup(String element) {
    return BinaryOperatorUtil.getCyclicGroup(element, this::prod);
  }
}
