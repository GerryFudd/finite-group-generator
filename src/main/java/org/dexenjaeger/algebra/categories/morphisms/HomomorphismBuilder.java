package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.model.OrderedPair;

public class HomomorphismBuilder extends BaseHomomorphismBuilder<Homomorphism> {
  
  
  @Override
  public Homomorphism build() {
    OrderedPair<int[], String[]> morphismSpec = resolveMapping();
    return new ConcreteHomomorphism(
      domain, range, kernel,
      morphismSpec.getLeft(), morphismSpec.getRight()
    );
  }
}
