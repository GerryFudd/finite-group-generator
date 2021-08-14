package org.dexenjaeger.algebra.model.cycle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Cycle<T> {
  int getSize();
  
  List<T> getElements();
  
  T get(int i);
  
  Set<T> getElementsSet();
  
  Set<T> getGenerators();
  
  Cycle<T> createCycle(int i);
  
  Set<? extends Cycle<T>> getSubCycles();
  
  Optional<? extends Cycle<T>> getSubCycleOfSize(int i);
  
  List<Integer> getSubCycleSizes();
  
  boolean isParentOf(Cycle<T> candidate);
  
  Optional<? extends Cycle<T>> getSubCycleGeneratedBy(T x);
}
