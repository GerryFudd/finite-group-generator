package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Map;

public class IntCycleBuilder extends AbstractCycleBuilder<Integer, IntCycle> {
  @Override
  Integer[] makeEmptyArray() {
    return new Integer[0];
  }
  
  @Override
  public IntCycle build() {
    OrderedPair<int[], Map<Integer, Integer>> generators = resolveGenerators();
    return new IntCycle(
      elements.length,
      elements,
      generators.getLeft(),
      generators.getRight()
    );
  }
}
