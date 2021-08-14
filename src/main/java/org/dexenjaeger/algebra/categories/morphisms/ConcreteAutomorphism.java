package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.HashSet;
import java.util.Set;

public class ConcreteAutomorphism extends ConcreteIsomorphism implements Automorphism {
  ConcreteAutomorphism(
    Group domain,
    int[] mapping,
    String[] image,
    int[] inverseMapping
  ) {
    super(domain, domain, mapping, image, inverseMapping);
  }
  
  @Override
  public int apply(int i) {
    return mapping[i];
  }
  
  @Override
  public int unApply(int j) {
    return inverseMapping[j];
  }
  
  @Override
  public Automorphism getInverse() {
    return Automorphism.builder()
             .inverseMapping(mapping)
             .mapping(inverseMapping)
             .domain(domain)
             .build();
  }
  
  @Override
  public Set<Integer> fixedElements() {
    Set<Integer> fixedElements = new HashSet<>();
    for (int i = 0; i < domain.getSize(); i++) {
      if (mapping[i] == i) {
        fixedElements.add(i);
      }
    }
    return fixedElements;
  }
}
