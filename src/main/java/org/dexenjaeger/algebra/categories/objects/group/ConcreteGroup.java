package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import org.dexenjaeger.algebra.model.Cycle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConcreteGroup implements Group {
  
  @Getter
  private final String operatorSymbol;
  @Getter
  private final int identity;
  @Getter
  private final int size;
  private final String[] elements;
  private final int[][] multiplicationTable;
  private final Map<String, Integer> lookup;
  private final Map<Integer, Integer> inversesMap;
  private final Set<Cycle> maximalCycles;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  
  ConcreteGroup(
    String operatorSymbol,
    int identity,
    String[] elements,
    int[][] multiplicationTable,
    Map<String, Integer> lookup,
    Map<Integer, Integer> inversesMap,
    Set<Cycle> maximalCycles,
    Map<Integer, Set<List<String>>> cyclesMap
  ) {
    this.operatorSymbol = operatorSymbol;
    this.identity = identity;
    this.elements = elements;
    this.multiplicationTable = multiplicationTable;
    this.lookup = lookup;
    this.size = elements.length;
    this.inversesMap = inversesMap;
    this.maximalCycles = maximalCycles;
    this.cyclesMap = cyclesMap;
  }
  
  @Override
  public int getInverse(int element) {
    return inversesMap.get(element);
  }
  
  @Override
  public int prod(int a, int b) {
    return multiplicationTable[a][b];
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
