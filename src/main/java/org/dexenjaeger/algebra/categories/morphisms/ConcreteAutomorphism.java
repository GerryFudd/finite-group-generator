package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.HashSet;
import java.util.Set;

public class ConcreteAutomorphism extends ConcreteIsomorphism implements Automorphism {
  private final CyclePresentation cyclePresentation;
  ConcreteAutomorphism(
    Group domain,
    int[] mapping,
    String[] image,
    int[] inverseMapping,
    CyclePresentation cyclePresentation
  ) {
    super(domain, domain, mapping, image, inverseMapping);
    this.cyclePresentation = cyclePresentation;
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
  public Set<Integer> fixedElements() {
    Set<Integer> fixedElements = new HashSet<>();
    for (int i = 0; i < domain.getSize(); i++) {
      if (mapping[i] == i) {
        fixedElements.add(i);
      }
    }
    return fixedElements;
  }
  
  @Override
  public String toString() {
    return cyclePresentation.toString();
  }
}
