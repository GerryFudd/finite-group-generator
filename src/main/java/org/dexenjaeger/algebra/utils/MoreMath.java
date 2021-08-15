package org.dexenjaeger.algebra.utils;

import java.util.Collection;
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
            "Computing %d! overflowed max int %d",
            n, Integer.MAX_VALUE
          ));
        }
        cache.put(n, result);
      }
    }
    return cache.get(n);
  }
  
  public static <T> Set<T> intersection(Collection<T> a, Collection<T> b) {
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
  
  public static int gcd(int m, int n) {
    if (m < 0 || n < 0) {
      throw new RuntimeException("This method is only implemented for positive inputs.");
    }
    int a = m;
    int b = n;
    while (a != 0 && b != 0) {
      a = a % b;
      int c = b;
      b = a;
      a = c;
    }
    if (a == 0) {
      return b;
    }
    return a;
  }
  
  public static int pow(int b, int n) {
    if (b < 0) {
      throw new RuntimeException("Exponential expressions may not have negative bases.");
    }
    if (b == 0) {
      return 0;
    }
    if (b == 1) {
      return 1;
    }
    if (n < 0) {
      throw new RuntimeException("Negative exponents are not defined for natural number bases above one.");
    }
    int result = 1;
    for (int i = 0; i < n; i++) {
      result *= b;
      if (result < 0) {
        throw new RuntimeException(String.format(
          "Exponent %d^%d overflowed max int %d.",
          b, i + 1, Integer.MAX_VALUE
        ));
      }
    }
    return result;
  }
}
