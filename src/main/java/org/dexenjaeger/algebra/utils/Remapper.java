package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Remapper {
  private final CycleUtils cycleUtils = new CycleUtils();
  @Getter
  private int currentIndex = 0;
  @Getter
  private final String[] elements;
  private final int[] remap;
  private final int[] inverseRemap;
  @Getter
  private final Map<String, Integer> reverseLookup;
  private final Set<Integer> available;
  
  private Remapper(String[] elements, int[] remap, int[] inverseRemap, Set<Integer> available, Map<String, Integer> reverseLookup) {
    this.elements = elements;
    this.remap = remap;
    this.inverseRemap = inverseRemap;
    this.available = available;
    this.reverseLookup = reverseLookup;
  }
  
  public static Remapper init(int n) {
    Set<Integer> available = new HashSet<>();
    for (int i = 0; i < n; i++) {
      available.add(i);
    }
    return new Remapper(
      new String[n],
      new int[n],
      new int[n],
      available,
      new HashMap<>()
    );
  }
  
  public Optional<String> map(String value, int oldIndex) {
    if (available.remove(oldIndex)) {
      elements[currentIndex] = value;
      remap[currentIndex] = oldIndex;
      inverseRemap[oldIndex] = currentIndex;
      reverseLookup.put(value, currentIndex);
      currentIndex++;
      return Optional.of(value);
    }
    return Optional.empty();
  }
  
  public BiFunction<Integer, Integer, Integer> remapBiFunc(BiFunction<Integer, Integer, Integer> biFunction) {
    return (a, b) -> inverseRemap[biFunction.apply(remap[a], remap[b])];
  }
  
  public Set<IntCycle> remapCycles(Set<IntCycle> oldCycles) {
    return oldCycles.stream()
             .map(oldCycle -> cycleUtils.convertToIntCycle(i -> inverseRemap[i], oldCycle))
             .collect(Collectors.toSet());
  }
  
  public Map<Integer, Integer> remapInverses(Map<Integer, Integer> inversesMap) {
    return inversesMap.entrySet()
      .stream()
      .collect(Collectors.toMap(
        entry -> inverseRemap[entry.getKey()],
        entry -> inverseRemap[entry.getValue()]
      ));
  }
}
