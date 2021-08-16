package org.dexenjaeger.algebra.model.cycle;

public class StringCycleBuilder extends AbstractCycleBuilder<String, StringCycle> {
  
  @Override
  protected String[] makeEmptyArray() {
    return new String[0];
  }
  
  @Override
  public StringCycle build() {
    return new StringCycle(
      elements.length,
      elements,
      lookup,
      generators,
      subCycleGenerators,
      maker
    );
  }
}
