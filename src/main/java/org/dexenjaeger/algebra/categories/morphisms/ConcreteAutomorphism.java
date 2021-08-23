package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.Element;

import java.util.Set;

public class ConcreteAutomorphism extends ConcreteIsomorphism implements Automorphism {
  @Getter
  private final CyclePresentation cyclePresentation;
  @Getter
  private final Set<Integer> fixedElements;
  @Getter
  private final boolean identity;
  
  ConcreteAutomorphism(
    Group domain,
    int[] mapping,
    Element[] image,
    int[] inverseMapping,
    Set<Integer> fixedElements,
    CyclePresentation cyclePresentation,
    boolean identity
  ) {
    super(domain, domain, mapping, image, inverseMapping);
    this.fixedElements = fixedElements;
    this.cyclePresentation = cyclePresentation;
    this.identity = identity;
  }
  
  @Override
  public String toString() {
    return cyclePresentation.toString();
  }
}
