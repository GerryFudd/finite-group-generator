package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.Mapping;

public class MappingCycleBuilder extends AbstractCycleBuilder<Mapping, MappingCycle> {
  @Override
  Mapping[] makeEmptyArray() {
    return new Mapping[0];
  }
  
  @Override
  public MappingCycle build() {
    return new MappingCycle(
      elements.length,
      elements,
      lookup,
      generators,
      subCycleGenerators,
      maker
    );
  }
}
