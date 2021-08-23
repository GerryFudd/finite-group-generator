package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.binaryoperator.Element;

public class ElementCycleBuilder extends AbstractCycleBuilder<Element, ElementCycle> {
  
  @Override
  protected Element[] makeEmptyArray() {
    return new Element[0];
  }
  
  @Override
  public ElementCycle build() {
    return new ElementCycle(
      elements.length,
      elements,
      lookup,
      generators,
      subCycleGenerators,
      maker
    );
  }
}
