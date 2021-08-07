package org.dexenjaeger.algebra.categories.objects;

public interface Group extends Monoid {
  String getInverse(String element);
}
