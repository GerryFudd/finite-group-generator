package org.dexenjaeger.algebra.categories.morphisms;

import java.util.Set;

public interface Automorphism extends Isomorphism {
  boolean isIdentity();
  CyclePresentation getCyclePresentation();
  Set<Integer> getFixedElements();
  static AutomorphismBuilder builder() {
    return new AutomorphismBuilder();
  }
}
