package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.List;
import java.util.Set;

public interface Group extends Monoid {
  String getInverse(String element);
  int getInverse(int element);
  List<Integer> getCycleSizes();
  Set<StringCycle> getNCycles(Integer n);
  Set<StringCycle> getMaximalCycles();
  
  static GroupBuilder builder() {
    return new GroupBuilder();
  }
}
