package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cycle {
  private final String[] elements;
  private final int[] generators;
  // Map of cycle size to a representative generator of the cycle of that size
  private final Map<Integer, Integer> subCycleGenerators;
  
  @EqualsAndHashCode.Include
  public List<String> getElements() {
    return List.of(elements);
  }
  
  public Set<String> getGenerators() {
    return Arrays.stream(generators)
             .mapToObj(i -> elements[i - 1])
             .collect(Collectors.toSet());
  }
  
  private Cycle createCycle(int i) {
    LinkedList<String> specList = new LinkedList<>();
    
    int current = i % elements.length;
    while (current != 0) {
      specList.addLast(elements[current - 1]);
      current = (i + current) % elements.length;
    }
    
    specList.addLast(elements[elements.length - 1]);
    
    return Cycle.builder()
             .elements(specList)
             .build();
  }
  
  public Set<Cycle> getSubCycles() {
    return subCycleGenerators.values()
             .stream()
             .map(this::createCycle)
             .collect(Collectors.toSet());
  }
  
  public Optional<Cycle> getSubCycleOfSize(int i) {
    return Optional.ofNullable(subCycleGenerators.get(i))
      .map(this::createCycle);
  }
  
  public static CycleBuilder builder() {
    return new CycleBuilder();
  }
}
