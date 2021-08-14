package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Group extends Monoid {
  String getInverse(String element);
  int getInverse(int element);
  List<Integer> getCycleSizes();
  Set<IntCycle> getNCycles(int n);
  Set<Integer> getNCycleGenerators(int n);
  Set<IntCycle> getMaximalCycles();
  Optional<IntCycle> getCycleGeneratedBy(int x);
  
  static GroupBuilder builder() {
    return new GroupBuilder();
  }
}
