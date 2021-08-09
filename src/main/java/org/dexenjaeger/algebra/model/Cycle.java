package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.dexenjaeger.algebra.utils.MoreMath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cycle {
  @Getter
  @EqualsAndHashCode.Include
  private final List<String> elements;
  
  public Set<String> getGenerators() {
    if (elements.size() == 1) {
      return Set.of(elements.get(0));
    }
    Set<String> generators = new HashSet<>();
    for (int i = 1; i <= (elements.size() + 1) / 2; i++) {
      if (MoreMath.gcd(i, elements.size()) == 1) {
        generators.add(elements.get(i - 1));
        generators.add(elements.get(elements.size() - i - 1));
      }
    }
    return generators;
  }
  
  public Set<Cycle> getSubCycles() {
    if (elements.size() == 1) {
      return Set.of();
    }
    Set<List<String>> cycleSpecs = new HashSet<>();
    for (int i = 2; i <= elements.size(); i++) {
      if (elements.size() % i == 0) {
        List<String> newCycleSpec = new ArrayList<>();
        int j = i;
        while (j % elements.size() != 0) {
          newCycleSpec.add(elements.get(j % elements.size() - 1));
          j += i;
        }
        newCycleSpec.add(elements.get(elements.size() - 1));
        cycleSpecs.add(newCycleSpec);
      }
    }
    return cycleSpecs.stream()
             .map(spec -> Cycle.builder()
                            .elements(spec)
                            .build())
             .collect(Collectors.toSet());
  }
}
