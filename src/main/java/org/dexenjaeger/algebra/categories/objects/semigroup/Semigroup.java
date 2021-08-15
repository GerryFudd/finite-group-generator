package org.dexenjaeger.algebra.categories.objects.semigroup;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

public interface Semigroup extends BinaryOperator {
  static SemigroupBuilder builder() {
    return new SemigroupBuilder();
  }
}
