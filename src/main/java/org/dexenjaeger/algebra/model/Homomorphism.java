package org.dexenjaeger.algebra.model;

public interface Homomorphism {
  Group getDomain();
  Group getRange();
  Group getKernel();
  String apply(String in);
}
