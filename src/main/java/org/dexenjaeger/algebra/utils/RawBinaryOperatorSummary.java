package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RawBinaryOperatorSummary {
  @Getter
  private final Set<Integer> leftIdentities = new HashSet<>();
  @Getter
  private final Set<Integer> rightIdentities = new HashSet<>();
  
  @Getter
  private final Set<IntCycle> cycles = new HashSet<>();
  
  public void addCycle(List<Integer> cycleElements) {
    if (cycleElements.isEmpty()) {
      return;
    }
    IntCycle candidateCycle = IntCycle.builder().elements(cycleElements).build();
    for (IntCycle cycle:cycles) {
      if (cycle.isParentOf(candidateCycle)) {
        return;
      }
    }
    cycles.removeIf(candidateCycle::isParentOf);
    cycles.add(candidateCycle);
  }
  
  public Optional<Integer> getIdentity() {
    return MoreMath.intersection(leftIdentities, rightIdentities)
      .stream().findAny();
  }
}
