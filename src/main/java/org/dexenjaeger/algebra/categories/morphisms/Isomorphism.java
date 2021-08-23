package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.model.Element;

public interface Isomorphism extends Homomorphism {
  int unApply(int j);
  Element unApply(Element y);
  
  static IsomorphismBuilder builder() {
    return new IsomorphismBuilder();
  }
}
