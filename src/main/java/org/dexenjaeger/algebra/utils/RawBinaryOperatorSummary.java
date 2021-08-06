package org.dexenjaeger.algebra.utils;

import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RawBinaryOperatorSummary {
  @Getter
  private final Set<Integer> leftIdentities = new HashSet<>();
  @Getter
  private final Set<Integer> rightIdentities = new HashSet<>();
  
  private final Map<Integer, Set<List<Integer>>> cycles = new HashMap<>();
  
  public void addCycle(List<Integer> cycle) {
    cycles.compute(cycle.size(), (n, nCycles) -> {
      if (nCycles == null) {
        nCycles = new HashSet<>();
      }
      if (nCycles.stream().anyMatch(nCycle -> nCycle.containsAll(cycle))) {
        return nCycles;
      }
      nCycles.add(cycle);
      return nCycles;
    });
  }
  
  public List<Integer> getCycleSizes() {
    return cycles.keySet().stream()
      .sorted()
      .collect(Collectors.toList());
  }
  
  public Set<List<Integer>> getNCycles(int n) {
    return cycles.get(n);
  }
  
  public Optional<Integer> getIdentity() {
    return MoreMath.intersection(leftIdentities, rightIdentities)
      .stream().findAny();
  }
}
