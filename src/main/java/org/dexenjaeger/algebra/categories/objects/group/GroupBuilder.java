package org.dexenjaeger.algebra.categories.objects.group;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.monoid.BaseMonoidBuilder;
import org.dexenjaeger.algebra.model.cycle.Cycle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupBuilder extends BaseMonoidBuilder<Group> {
  private Map<Integer, Integer> inversesMap;
  
  private Map<Integer, Set<List<String>>> cyclesMap;
  private Set<Cycle> maximalCycles;
  
  public GroupBuilder inversesMap(Map<Integer, Integer> inversesMap) {
    this.inversesMap = inversesMap;
    return this;
  }
  
  public GroupBuilder cyclesMap(Map<Integer, Set<List<String>>> cyclesMap) {
    this.cyclesMap = cyclesMap;
    return this;
  }
  
  public GroupBuilder maximalCycles(Set<Cycle> maximalCycles) {
    this.maximalCycles = maximalCycles;
    return this;
  }
  
  protected Set<Cycle> resolveMaxCycles() {
    if (maximalCycles != null) {
      return maximalCycles;
    }
    if (resolveSize() == 1) {
      String[] el = resolveElements();
      return Set.of(Cycle.builder()
                      .elements(List.of(el[identity]))
                      .build());
    }
    return cyclesMap.entrySet().stream()
             .filter(entry -> entry.getKey() > 1)
             .map(Map.Entry::getValue)
             .flatMap(Set::stream)
             .map(cycleElements -> Cycle.builder()
                                     .elements(cycleElements)
                                     .build())
             .collect(Collectors.toSet());
    
  }
  
  @Override
  public Group build() {
    return new ConcreteGroup(
      operatorSymbol, resolveSize(), resolveElements(),
      resolveLookup(), resolveMultiplicationTable(), identity,
      inversesMap, resolveMaxCycles(), cyclesMap
    );
  }
}
