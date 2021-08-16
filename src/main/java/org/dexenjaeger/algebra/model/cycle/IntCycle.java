package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class IntCycle extends AbstractCycle<Integer, IntCycle> {
  public IntCycle(
    int size, Integer[] elements, Map<Integer, Integer> lookup,
    int[] generators, Map<Integer, Integer> subCycleGenerators,
    Function<List<Integer>, IntCycle> maker
    ) {
    super(
      size, elements, lookup, generators, subCycleGenerators,
      maker
    );
  }
  
  public static IntCycleBuilder builder() {
    return new IntCycleBuilder();
  }
  
  @Override
  public String toString() {
    return getElements().toString();
  }
}
