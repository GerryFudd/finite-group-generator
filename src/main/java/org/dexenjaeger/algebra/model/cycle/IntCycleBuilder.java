package org.dexenjaeger.algebra.model.cycle;

public class IntCycleBuilder extends AbstractCycleBuilder<Integer, IntCycle> {
  @Override
  Integer[] makeEmptyArray() {
    return new Integer[0];
  }
  
  @Override
  public IntCycle build() {
    return new IntCycle(
      elements.length,
      elements,
      lookup,
      generators,
      subCycleGenerators,
      maker
    );
  }
}
