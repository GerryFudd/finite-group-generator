package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.model.OrderedPair;

public class IsomorphismBuilder extends BaseIsomorphismBuilder<Isomorphism> {
  
  @Override
  public Isomorphism build() {
    OrderedPair<int[], String[]> morphismSpec = resolveMapping();
    
    return new ConcreteIsomorphism(
      domain, range, mapping,
      resolveImage(), resolveInverseMapping()
    );
  }
}
