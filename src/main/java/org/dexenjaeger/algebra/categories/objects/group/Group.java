package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;

import java.util.List;
import java.util.Set;

public interface Group extends Monoid {
  String getInverse(String element);
  List<Integer> getCycleSizes();
  Set<List<String>> getNCycles(Integer n);
}
