package org.dexenjaeger.algebra.categories.morphisms;

public interface Isomorphism extends Homomorphism {
  int unApply(int b);
  
  static IsomorphismBuilder builder() {
    return new IsomorphismBuilder();
  }
}
