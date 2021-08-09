package org.dexenjaeger.algebra.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BinaryOperatorService {
  public BiFunction<String, String, String> createOperator(
    String[] elements, BiFunction<Integer, Integer, Integer> intOp
  ) {
    Map<String, Integer> lookup = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      lookup.put(elements[i], i);
    }
    return (a, b) -> elements[intOp.apply(lookup.get(a), lookup.get(b))];
  }
}
