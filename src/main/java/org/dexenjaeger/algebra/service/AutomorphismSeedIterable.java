package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.model.TripleLookup;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.OrderedPairIterable;
import org.dexenjaeger.algebra.utils.PermutationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutomorphismSeedIterable implements Iterable<Map<Integer, Integer>> {
  // cycle generator lookup (cycleSizeIndex, generatorWithinCycleIndex) -> cycleGeneratorIndex
  private final TripleLookup cycleGeneratorByIndices;
  // A list of iterables over cycle generator lookups.
  // Each lookup is a pair of
  // (permutation of cycles within a size index, mapping of cycle to the target generator)
  private final List<Iterable<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterables;
  
  private AutomorphismSeedIterable(
    TripleLookup cycleGeneratorByIndices,
    List<Iterable<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterables
  ) {
    this.cycleGeneratorByIndices = cycleGeneratorByIndices;
    this.cycleGeneratorLookupIterables = cycleGeneratorLookupIterables;
  }
  
  /**
   * Convert the set of cycles into the info that we need to generate automorphism seeds.
   * Each automorphism seed is a subset of the overall mapping of each element to each
   * other element. This seed is constructed in such a way that it will necessarily specify
   * the entire automorphism (since it maps a cycle generator for each maximal cycle) and
   * it restricts the possible images of these elements as much as is reasonable. No two
   * elements are mapped to the same other element. The image of each element is has the
   * same order as the element itself. No two generators of the same cycle are explicitly
   * mapped to.
   * @param cycles: a collection of all of the maximal cycles from a group
   * @return an iterable that will loop through all seeds for an automorphism on this group
   * that obey the above restrictions.
   */
  public static AutomorphismSeedIterable init(
    Collection<IntCycle> cycles
  ) {
    Map<Integer, Integer> generatorCountByCycleSize = new HashMap<>();
    Map<Integer, Integer> cycleCountByCycleSize = new HashMap<>();
    Map<Integer, Map<IntCycle, List<Integer>>> sortedIntCycleGeneratorsByCycleSizeAndIntCycle = new HashMap<>();
    for (IntCycle cycle:cycles) {
      generatorCountByCycleSize.computeIfAbsent(cycle.getSize(), key -> cycle.getGenerators().size());
      cycleCountByCycleSize.computeIfPresent(cycle.getSize(), (key, val) -> val + 1);
      cycleCountByCycleSize.putIfAbsent(cycle.getSize(), 1);
      sortedIntCycleGeneratorsByCycleSizeAndIntCycle.computeIfPresent(
        cycle.getSize(), (key, val) -> {
          val.put(cycle, cycle.getGenerators().stream().sorted().collect(Collectors.toList()));
          return val;
        }
      );
      sortedIntCycleGeneratorsByCycleSizeAndIntCycle.computeIfAbsent(
        cycle.getSize(), key -> {
          Map<IntCycle, List<Integer>> val = new HashMap<>();
          val.put(cycle, cycle.getGenerators().stream().sorted().collect(Collectors.toList()));
          return val;
        }
      );
    }
    List<Integer> sortedCycleSizes = generatorCountByCycleSize.keySet().stream().sorted().collect(Collectors.toList());
    List<List<List<Integer>>> cycleGeneratorLookupTable = new ArrayList<>(sortedCycleSizes.size());
    for (Integer sortedCycleSize : sortedCycleSizes) {
      cycleGeneratorLookupTable.add(
        sortedIntCycleGeneratorsByCycleSizeAndIntCycle
          .get(sortedCycleSize)
          .entrySet()
          .stream()
          .sorted(Comparator.comparing(cycle -> cycle.getKey().get(0)))
          .map(Map.Entry::getValue)
          .collect(Collectors.toList())
      );
    }
    
    List<Iterable<OrderedPair<Mapping, Mapping>>> cycleGeneratorLookupIterables = new ArrayList<>();
    for (Integer cycleSize:sortedCycleSizes) {
      cycleGeneratorLookupIterables.add(
        new OrderedPairIterable<>(
          PermutationUtil.getPermutationIterable(cycleCountByCycleSize.get(cycleSize)),
          FunctionsUtil.getFunctionIterable(
            cycleCountByCycleSize.get(cycleSize), generatorCountByCycleSize.get(cycleSize)
          )
        )
      );
    }
    
    return new AutomorphismSeedIterable(
      (i, j, k) -> cycleGeneratorLookupTable.get(i).get(j).get(k),
      cycleGeneratorLookupIterables
    );
  }
  
  @Override
  public Iterator<Map<Integer, Integer>> iterator() {
    return new AutomorphismSeedIterator(cycleGeneratorByIndices, cycleGeneratorLookupIterables);
  }
}
