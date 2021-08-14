package org.dexenjaeger.algebra.utils;

import lombok.Getter;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.validators.BinaryOperatorValidator;
import org.dexenjaeger.algebra.validators.ValidationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Remapper {
  private final BinaryOperatorValidator validator = new BinaryOperatorValidator();
  @Getter
  private int currentIndex = 0;
  private int defaultIndicesUsed = 0;
  private final String[] elements;
  private final int[] remap;
  private final int[] inverseRemap;
  @Getter
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
  
  public BinaryOperator createBinaryOperator(BiFunction<Integer, Integer, Integer> binOp) {
    BinaryOperator result = BinaryOperator.builder()
                              .elements(elements)
                              .lookup(reverseLookup)
                              .operator((a, b) -> inverseRemap[binOp.apply(remap[a], remap[b])])
                              .build();
    try {
      validator.validate(result);
      return result;
    } catch (ValidationException e) {
      throw new RuntimeException(
        "Binary operator failed to validate",
        e
      );
    }
  }
  
  public Set<IntCycle> remapCycles(Set<IntCycle> oldCycles) {
    return oldCycles.stream()
             .map(oldCycle -> IntCycle.builder().elements(
               oldCycle.getElements().stream()
                 .map(i -> inverseRemap[i])
                 .collect(Collectors.toList())
             ).build())
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
