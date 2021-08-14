package org.dexenjaeger.algebra.model.cycle;

public class IntCycleBuilder extends AbstractCycleBuilder<Integer, IntCycle> {
  @Override
  Integer[] makeEmptyArray() {
    return new Integer[0];
  }
  
  public IntCycleBuilder elements(Integer... elements) {
    this.elements = elements;
    return this;
  }
  
  @Override
  public IntCycle build() {
    CycleSpec<Integer> spec = resolveGenerators();
    return new IntCycle(
      elements.length,
      elements,
      spec.getLookup(),
      spec.getGeneratorArray(),
      spec.getSubCycleGenerators()
    );
  }
}
