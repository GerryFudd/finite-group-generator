package org.dexenjaeger.algebra.categories.objects.group;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.cycle.Cycle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
  private final Set<Cycle> maximalCycles;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  
  
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
    return cyclesMap.keySet().stream().sorted().collect(Collectors.toList());
  }
  
  @Override
  public Set<List<String>> getNCycles(Integer n) {
    return cyclesMap.containsKey(n) ? cyclesMap.get(n) : Set.of();
  }
  
  @Override
  public Set<Cycle> getMaximalCycles() {
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
  
  public static GroupBuilder builder() {
    return new GroupBuilder();
  }
}
