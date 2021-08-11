package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.cycle.Cycle;

import java.util.List;
import java.util.Set;

public interface Group extends Monoid {
  String getInverse(String element);
  int getInverse(int element);
  List<Integer> getCycleSizes();
  Set<List<String>> getNCycles(Integer n);
  Set<Cycle> getMaximalCycles();
  
  static GroupBuilder builder() {
    return new GroupBuilder();
  }
}
