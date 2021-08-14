package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;

public class AutomorphismBuilder extends BaseIsomorphismBuilder<Automorphism> {
  @Override
  public AutomorphismBuilder range(Group range) {
    throw new RuntimeException("Not implemented.");
  }
  
  @Override
  public Automorphism build() {
    range = domain;
    OrderedPair<int[], String[]> morphismSpec = resolveMapping();
    return new ConcreteAutomorphism(
      domain, morphismSpec.getLeft(),
      morphismSpec.getRight(), resolveInverseMapping()
    );
  }
}
