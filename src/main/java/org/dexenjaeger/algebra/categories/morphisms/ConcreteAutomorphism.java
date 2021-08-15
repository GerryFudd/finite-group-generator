package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.Set;

public class ConcreteAutomorphism extends ConcreteIsomorphism implements Automorphism {
  @Getter
  private final CyclePresentation cyclePresentation;
  @Getter
  private final Set<Integer> fixedElements;
  
  ConcreteAutomorphism(
    Group domain,
    int[] mapping,
    String[] image,
    int[] inverseMapping,
    Set<Integer> fixedElements,
    CyclePresentation cyclePresentation
  ) {
    super(domain, domain, mapping, image, inverseMapping);
    this.fixedElements = fixedElements;
    this.cyclePresentation = cyclePresentation;
  }
  
  @Override
  public String toString() {
    return cyclePresentation.toString();
  }
}
