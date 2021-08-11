package org.dexenjaeger.algebra.categories.objects.group;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ConcreteGroup implements Group {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final int size;
  private final String[] elements;
  private final Map<String, Integer> lookup;
  private final int[][] multiplicationTable;
  @Getter
  private final int identity;
  private final Map<Integer, Integer> inversesMap;
  private final Set<StringCycle> maximalCycles;
  
  
  @Override
  public int getInverse(int element) {
    return inversesMap.get(element);
  }
  
  @Override
  public int prod(int a, int b) {
    return multiplicationTable[a][b];
  }
  
  @Override
  public Integer eval(String a) {
    return lookup.get(a);
  }
  
  @Override
  public String display(int i) {
    return elements[i];
  }
  
  @Override
  public List<Integer> getCycleSizes() {
    return maximalCycles.stream()
             .flatMap(cycle -> Stream.concat(
               Stream.of(cycle.getSize()),
               cycle.getSubCycleSizes().stream()
             ))
             .collect(Collectors.toSet())
             .stream()
             .sorted()
             .collect(Collectors.toList());
  }
  
  @Override
  public Set<StringCycle> getNCycles(Integer n) {
    return maximalCycles.stream()
             .map(cycle -> cycle.getSize() == n ?
                             Optional.of(cycle) :
                             cycle.getSubCycleOfSize(n))
             .filter(Optional::isPresent)
             .map(Optional::get)
             .collect(Collectors.toSet());
  }
  
  @Override
  public Set<StringCycle> getMaximalCycles() {
    return maximalCycles;
  }
  
  @Override
  public Set<String> getElementsDisplay() {
    return Set.of(elements);
  }
  
  @Override
  public String getIdentityDisplay() {
    return elements[identity];
  }
  
  @Override
  public String getInverse(String element) {
    return elements[getInverse(lookup.get(element))];
  }
  
  @Override
  public String prod(String a, String b) {
    return elements[prod(lookup.get(a), lookup.get(b))];
  }
}
