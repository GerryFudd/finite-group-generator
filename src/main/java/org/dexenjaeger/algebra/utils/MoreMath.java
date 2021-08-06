package org.dexenjaeger.algebra.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MoreMath {
  private static Map<Integer, Integer> cache = new HashMap<>();
  public static int factorial(int n) {
    if (n < 0) {
      throw new RuntimeException("No.");
    }
    if (!cache.containsKey(n)) {
      if (n == 0) {
        cache.put(0, 1);
      } else {
        int previous = factorial(n - 1);
        int result = n * previous;
        if (result < previous) {
          throw new RuntimeException(String.format(
            "Computing %d! overflowed max long %d",
            n, Integer.MAX_VALUE
          ));
        }
        cache.put(n, result);
      }
    }
    return cache.get(n);
  }
  
  public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
    if (a == null && b == null) {
      return null;
    }
    if (a == null) {
      return new HashSet<>(b);
    }
    if (b == null) {
      return new HashSet<>(a);
    }
    return a.stream()
      .filter(b::contains)
      .collect(Collectors.toSet());
  }
}
