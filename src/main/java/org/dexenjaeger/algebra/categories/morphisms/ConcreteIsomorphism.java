package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;

public class ConcreteIsomorphism extends ConcreteHomomorphism implements Isomorphism {
  protected final int[] inverseMapping;
  
  ConcreteIsomorphism(
    Group domain,
    Group range,
    int[] mapping,
    String[] image,
    int[] inverseMapping
  ) {
    super(
      domain, range,
      new TrivialGroup(domain.getIdentityDisplay()),
      mapping, image
    );
    this.inverseMapping = inverseMapping;
  }
  
  @Override
  public Isomorphism getInverse() {
    return Isomorphism.builder()
             .inverseMapping(mapping)
             .range(domain)
             .domain(range)
             .mapping(inverseMapping)
             .build();
  }
  
  @Override
  public int unApply(int j) {
    return inverseMapping[j];
  }
}
