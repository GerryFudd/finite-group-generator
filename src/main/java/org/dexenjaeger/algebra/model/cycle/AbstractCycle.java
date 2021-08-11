package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractCycle<T, U extends Cycle> implements Cycle {
  @Getter
  protected final int size;
  protected final T[] elements;
  protected final int[] generators;
  // Map of cycle size to a representative generator of the cycle of that size
  protected final Map<Integer, Integer> subCycleGenerators;
  protected final Function<List<T>, U> make;
  
  public List<T> getElements() {
    return List.of(elements);
  }
  
  public T get(int i) {
    if (i < 0) {
      return elements[size - (i % size)];
    }
    return elements[i % size];
  }
  
  @EqualsAndHashCode.Include
  public Set<T> getElementsSet() {
    return Set.of(elements);
  }
  
  @EqualsAndHashCode.Include
  public Set<T> getGenerators() {
    return Arrays.stream(generators)
             .mapToObj(i -> elements[i - 1])
             .collect(Collectors.toSet());
  }
  
  private U createCycle(int i) {
    LinkedList<T> specList = new LinkedList<>();
    
    int current = i % elements.length;
    while (current != 0) {
      specList.addLast(elements[current - 1]);
      current = (i + current) % elements.length;
    }
    
    specList.addLast(elements[elements.length - 1]);
    
    return make.apply(specList);
  }
  
  public Set<U> getSubCycles() {
    return subCycleGenerators.values()
             .stream()
             .map(this::createCycle)
             .collect(Collectors.toSet());
  }
  
  public Optional<U> getSubCycleOfSize(int i) {
    return Optional.ofNullable(subCycleGenerators.get(i))
             .map(this::createCycle);
  }
  
  public List<Integer> getSubCycleSizes() {
    return subCycleGenerators.keySet().stream().sorted().collect(Collectors.toList());
  }
  
  public boolean isParentOf(U candidate) {
    return getSubCycleOfSize(candidate.getSize())
             .stream()
             .anyMatch(subCycle -> subCycle.equals(candidate));
  }
}
