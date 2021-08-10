package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.List;

public interface Semigroup extends BinaryOperator {
  default List<String> getCycleElementsDisplay(String element) {
    return BinaryOperatorUtil.getCycle(element, this::prod);
  }
}
