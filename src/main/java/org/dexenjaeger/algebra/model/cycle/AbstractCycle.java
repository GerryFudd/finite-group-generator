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
public abstract class AbstractCycle<T, U extends Cycle<T>> implements Cycle<T> {
  @Getter
  protected final int size;
  protected final T[] elements;
  protected final Map<T, Integer> lookup;
  protected final int[] generators;
  // Map of cycle size to a representative generator of the cycle of that size
  protected final Map<Integer, Integer> subCycleGenerators;
  protected final Function<List<T>, U> make;
  
  @Override
  public List<T> getElements() {
    return List.of(elements);
  }
  
  @Override
  public T get(int i) {
    if (i < 0) {
      return elements[size - 1 + (i % size)];
    }
    return elements[i % size];
  }
  
  @Override
  @EqualsAndHashCode.Include
  public Set<T> getElementsSet() {
    return Set.of(elements);
  }
  
  @Override
  @EqualsAndHashCode.Include
  public Set<T> getGenerators() {
    return Arrays.stream(generators)
             .mapToObj(i -> get(i - 1))
             .collect(Collectors.toSet());
  }
  
  @Override
  public U createCycle(int i) {
    LinkedList<T> specList = new LinkedList<>();
    
    int current = i % elements.length;
    while (current != 0) {
      specList.addLast(elements[current - 1]);
      current = (i + current) % elements.length;
    }
    
    specList.addLast(elements[elements.length - 1]);
    
    return make.apply(specList);
  }
  
  @Override
  public Set<U> getSubCycles() {
    return subCycleGenerators.values()
             .stream()
             .map(this::createCycle)
             .collect(Collectors.toSet());
  }
  
  @Override
  public Optional<U> getSubCycleOfSize(int i) {
    return Optional.ofNullable(subCycleGenerators.get(i))
             .map(this::createCycle);
  }
  
  @Override
  public List<Integer> getSubCycleSizes() {
    return subCycleGenerators.keySet().stream().sorted().collect(Collectors.toList());
  }
  
  @Override
  public boolean isParentOf(Cycle<T> candidate) {
    return getSubCycleOfSize(candidate.getSize())
             .stream()
             .anyMatch(subCycle -> subCycle.equals(candidate));
  }
  
  @Override
  public Optional<U> getSubCycleGeneratedBy(T x) {
    return Optional.ofNullable(lookup.get(x))
      .map(this::createCycle);
  }
}
