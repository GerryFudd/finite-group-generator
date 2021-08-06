package org.dexenjaeger.algebra.model;

public interface Group extends Monoid {
  String getInverse(String element);
}
