package org.dexenjaeger.algebra.model;

@FunctionalInterface
public interface TripleLookup {
  int lookup(int i, int j, int k);
}
