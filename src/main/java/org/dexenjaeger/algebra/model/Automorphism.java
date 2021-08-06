package org.dexenjaeger.algebra.model;

public interface Automorphism extends Homomorphism {
  Automorphism getInverse();
}
