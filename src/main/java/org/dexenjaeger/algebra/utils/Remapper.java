package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import org.dexenjaeger.algebra.model.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class Remapper {
  @Getter
  private int currentIndex = 0;
  @Getter
  private final Element[] elements;
  private final int[] remap;
  private final int[] inverseRemap;
  @Getter
  private final Map<Element, Integer> reverseLookup;
  private final Set<Integer> available;
  
  private Remapper(Element[] elements, int[] remap, int[] inverseRemap, Set<Integer> available, Map<Element, Integer> reverseLookup) {
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
      new Element[n],
      new int[n],
      new int[n],
      available,
      new HashMap<>()
    );
  }
  
  public Optional<Element> map(Element value, int oldIndex) {
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
}
