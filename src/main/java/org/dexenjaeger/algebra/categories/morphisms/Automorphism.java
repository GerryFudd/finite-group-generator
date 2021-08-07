package org.dexenjaeger.algebra.categories.morphisms;

public interface Automorphism extends Homomorphism {
  Automorphism getInverse();
}
