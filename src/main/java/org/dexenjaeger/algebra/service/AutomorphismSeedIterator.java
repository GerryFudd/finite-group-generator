package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.model.TripleLookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutomorphismSeedIterator implements Iterator<Map<Integer, Integer>> {
  // cycle generator lookup (cycleSizeIndex, cycleWithinSizeIndex, generatorWithinCycleIndex) -> cycleGeneratorIndex
  private final TripleLookup cycleGeneratorByIndices;
  // A list of iterables over cycle generator lookups.
  // Each lookup is a pair of
  // (permutation of cycles within a size index, mapping of cycle to the target generator)
  private final List<Iterable<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterables;
  
  private List<Iterator<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterators;
  private List<OrderedPair<Mapping, Mapping>> currentCycleLookups;
  
  AutomorphismSeedIterator(TripleLookup cycleGeneratorByIndices, List<Iterable<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterables) {
    this.cycleGeneratorByIndices = cycleGeneratorByIndices;
    this.cycleGeneratorLookupIterables = cycleGeneratorLookupIterables;
  }
  
  @Override
  public boolean hasNext() {
    if (cycleGeneratorLookupIterators == null) {
      return true;
    }
    return cycleGeneratorLookupIterators.stream().anyMatch(Iterator::hasNext);
  }
  
  private void resetIteratorAtIndex(int i) {
    cycleGeneratorLookupIterators.set(
      i,
      cycleGeneratorLookupIterables.get(i).iterator()
    );
  }
  
  private boolean hasNext(int i) {
    return cycleGeneratorLookupIterators.get(i).hasNext();
  }
  
  private void update(int i) {
    currentCycleLookups.set(i, cycleGeneratorLookupIterators.get(i).next());
  }
  
  private Map<Integer, Integer> getSeedFromCurrentMapping() {
    Map<Integer, Integer> seed = new HashMap<>();
    for (int s = 0; s < currentCycleLookups.size(); s++) {
      OrderedPair<Mapping, Mapping> cycleGeneratorLookup = currentCycleLookups.get(s);
      // This is the list of cycles that are shuffled by the mapping spec
      Mapping permutationOfCycles = cycleGeneratorLookup.getLeft();
      Mapping choiceOfGeneratorsWithinCycles = cycleGeneratorLookup.getRight();
      for (int i = 0; i < permutationOfCycles.size(); i++) {
        seed.put(
          cycleGeneratorByIndices.lookup(s, i, 0),
          cycleGeneratorByIndices.lookup(
            s,
            permutationOfCycles.get(i),
            choiceOfGeneratorsWithinCycles.get(permutationOfCycles.get(i))
          )
        );
      }
    }
    return seed;
  }
  
  @Override
  public Map<Integer, Integer> next() {
    if (cycleGeneratorLookupIterators == null) {
      cycleGeneratorLookupIterators = cycleGeneratorLookupIterables
                                        .stream()
                                        .map(Iterable::iterator)
                                        .collect(Collectors.toList());
      currentCycleLookups = cycleGeneratorLookupIterators
                              .stream()
                              .map(Iterator::next)
                              .collect(Collectors.toList());
      return getSeedFromCurrentMapping();
    }
    int i = 0;
    while (
      i < cycleGeneratorLookupIterators.size()
        && !hasNext(i)
    ) {
      resetIteratorAtIndex(i);
      update(i);
      i++;
    }
    update(i);
    
    return getSeedFromCurrentMapping();
  }
}
