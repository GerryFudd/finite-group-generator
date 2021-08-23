package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.AbstractCycle;
import org.dexenjaeger.algebra.model.cycle.ElementCycle;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AutomorphismBuilder extends BaseIsomorphismBuilder<Automorphism> {
  private Set<Integer> fixedElements;
  
  public AutomorphismBuilder fixedElements(Set<Integer> fixedElements) {
    this.fixedElements = fixedElements;
    return this;
  }
  
  private final Set<ElementCycle> elementCycles = new HashSet<>();
  @Override
  public AutomorphismBuilder range(Group range) {
    throw new RuntimeException("Not implemented.");
  }
  
  public AutomorphismBuilder withElementCycles(ElementCycle... cycles) {
    return withElementCycles(Set.of(cycles));
  }
  
  public AutomorphismBuilder withElementCycles(Collection<ElementCycle> cycles) {
    elementCycles.addAll(cycles);
    return this;
  }
  
  private boolean identity;
  
  public AutomorphismBuilder identity(boolean identity) {
    this.identity = identity;
    return this;
  }
  
  @Override
  public Automorphism build() {
    return new ConcreteAutomorphism(
      domain, mapping, image, inverseMapping,
      fixedElements,
      new CyclePresentation(elementCycles.stream()
                              .sorted(Comparator.comparing(ElementCycle::toString))
                              .sorted(Comparator.comparing(AbstractCycle::getSize))
                              .collect(Collectors.toList())),
      identity
    );
  }
}
