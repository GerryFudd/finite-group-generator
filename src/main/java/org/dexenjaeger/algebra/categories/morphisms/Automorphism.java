package org.dexenjaeger.algebra.categories.morphisms;

import java.util.Set;

public interface Automorphism extends Isomorphism {
  Set<Integer> fixedElements();
  static AutomorphismBuilder builder() {
    return new AutomorphismBuilder();
  }
}
