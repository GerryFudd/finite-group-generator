package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Map;

public class StringCycleBuilder extends AbstractCycleBuilder<String, StringCycle> {
  
  @Override
  protected String[] makeEmptyArray() {
    return new String[0];
  }
  
  public StringCycleBuilder elements(String... elements) {
    this.elements = elements;
    return this;
  }
  
  @Override
  public StringCycle build() {
    OrderedPair<int[], Map<Integer, Integer>> generatorArrays = resolveGenerators();
    return new StringCycle(
      elements.length,
      elements,
      generatorArrays.getLeft(),
      generatorArrays.getRight()
    );
  }
}
