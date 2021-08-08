package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import org.dexenjaeger.algebra.model.ValidatingBinaryOperator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class Remapper {
  private int currentIndex = 0;
  private int defaultIndicesUsed = 0;
  private final String[] elements;
  private final int[] remap;
  private final int[] inverseRemap;
  private final Map<String, Integer> reverseLookup;
  @Getter
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
  
  private static String getSymbolForNumber(int i) {
    int n = i;
    StringBuilder result = new StringBuilder();
    result.insert(0, (char) (n % 26 + 97));
    while (n / 26 > 0) {
      n = n / 26;
      result.insert(0, (char) ((n - 1) % 26 + 97));
    }
    return result.toString();
  }
  
  public Optional<String> map(int oldIndex) {
    Optional<String> result = map(getSymbolForNumber(defaultIndicesUsed), oldIndex);
    if (result.isPresent()) {
      defaultIndicesUsed++;
    }
    return result;
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
  
  public ValidatingBinaryOperator createBinaryOperator(BiFunction<Integer, Integer, Integer> binOp) {
    return new ValidatingBinaryOperator(
      elements,
      reverseLookup,
      (a, b) -> inverseRemap[binOp.apply(remap[a], remap[b])]
    );
  }
}
