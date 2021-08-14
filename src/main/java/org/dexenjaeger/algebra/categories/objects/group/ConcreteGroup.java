package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperator;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConcreteGroup extends BaseBinaryOperator implements Group {
  @Getter
  private final int identity;
  private final Map<Integer, Integer> inversesMap;
  private final Set<IntCycle> maximalCycles;
  
  ConcreteGroup(
    String operatorSymbol,
    int size,
    String[] elements,
    int identity,
    Map<Integer, Integer> inversesMap,
    Set<IntCycle> maximalCycles,
    Map<String, Integer> lookup,
    int[][] multiplicationTable
  ) {
    super(
      operatorSymbol, size, elements,
      lookup, multiplicationTable
    );
    this.identity = identity;
    this.inversesMap = inversesMap;
    this.maximalCycles = maximalCycles;
  }
  
  
  @Override
  public int getInverse(int element) {
    return inversesMap.get(element);
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
  
  private Stream<IntCycle> getNCycleStream(int n) {
    return maximalCycles.stream()
             .map(cycle -> cycle.getSize() == n ?
                             Optional.of(cycle) :
                             cycle.getSubCycleOfSize(n))
             .filter(Optional::isPresent)
             .map(Optional::get);
  }
  
  @Override
  public Set<IntCycle> getNCycles(int n) {
    return getNCycleStream(n).collect(Collectors.toSet());
  }
  
  @Override
  public Set<Integer> getNCycleGenerators(int n) {
    return getNCycleStream(n)
      .map(IntCycle::getGenerators)
      .flatMap(Set::stream)
      .collect(Collectors.toSet());
  }
  
  @Override
  public Set<IntCycle> getMaximalCycles() {
    return maximalCycles;
  }
  
  @Override
  public Optional<IntCycle> getCycleGeneratedBy(int x) {
    return maximalCycles.stream()
      .map(cycle -> cycle.getSubCycleGeneratedBy(x))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findAny();
  }
  
  @Override
  public String getIdentityDisplay() {
    return elements[identity];
  }
  
  @Override
  public String getInverse(String element) {
    return elements[getInverse(lookup.get(element))];
  }
}
