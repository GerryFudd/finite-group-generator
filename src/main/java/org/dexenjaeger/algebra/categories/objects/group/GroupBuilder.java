package org.dexenjaeger.algebra.categories.objects.group;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.monoid.BaseMonoidBuilder;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupBuilder extends BaseMonoidBuilder<Group> {
  private Map<Integer, Integer> inversesMap;
  private Set<IntCycle> maximalCycles;
  private Set<StringCycle> maximalDisplayCycles;
  
  public GroupBuilder inversesMap(Map<Integer, Integer> inversesMap) {
    this.inversesMap = inversesMap;
    return this;
  }
  
  public GroupBuilder maximalCycles(Set<IntCycle> maximalCycles) {
    this.maximalCycles = maximalCycles;
    return this;
  }
  
  @Override
  public Group build() {
    return new ConcreteGroup(
      operatorSymbol, resolveSize(), resolveElements(), identity,
      inversesMap, maximalCycles,
      resolveLookup(), resolveMultiplicationTable()
    );
  }
}
