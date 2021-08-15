package org.dexenjaeger.algebra.categories.morphisms;

public interface Isomorphism extends Homomorphism {
  int unApply(int j);
  String unApply(String y);
  
  static IsomorphismBuilder builder() {
    return new IsomorphismBuilder();
  }
}
