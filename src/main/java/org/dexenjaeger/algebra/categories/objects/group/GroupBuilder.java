package org.dexenjaeger.algebra.categories.objects.group;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.monoid.BaseMonoidBuilder;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupBuilder extends BaseMonoidBuilder<Group> {
  private Map<Integer, Integer> inversesMap;
  private Set<StringCycle> maximalCycles;
  
  public GroupBuilder inversesMap(Map<Integer, Integer> inversesMap) {
    this.inversesMap = inversesMap;
    return this;
  }
  
  public GroupBuilder maximalCycles(Set<StringCycle> maximalCycles) {
    this.maximalCycles = maximalCycles;
    return this;
  }
  
  @Override
  public Group build() {
    return new ConcreteGroup(
      operatorSymbol, resolveSize(), resolveElements(),
      resolveLookup(), resolveMultiplicationTable(), identity,
      inversesMap, maximalCycles
    );
  }
}
