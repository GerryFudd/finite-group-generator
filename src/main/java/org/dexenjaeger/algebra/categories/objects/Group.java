package org.dexenjaeger.algebra.categories.objects;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Group extends Monoid {
  String getInverse(String element);
  List<Integer> getCycleSizes();
  Set<List<String>> getNCycles(Integer n);
}
