package org.dexenjaeger.algebra.categories.morphisms;

public interface Isomorphism extends Homomorphism {
  int unApply(int b);
  
  Isomorphism getInverse();
  
  static IsomorphismBuilder builder() {
    return new IsomorphismBuilder();
  }
}
