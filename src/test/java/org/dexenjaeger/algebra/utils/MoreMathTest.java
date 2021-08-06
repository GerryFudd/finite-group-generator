package org.dexenjaeger.algebra.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MoreMathTest {
  @Test
  void factorialInvalidInput() {
    assertThrows(RuntimeException.class, () -> MoreMath.factorial(-1));
  }
  
  @Test
  void factorialOverflow() {
    assertEquals(
      1932053504,
      MoreMath.factorial(13)
    );
    assertThrows(RuntimeException.class, () -> MoreMath.factorial(14));
  }
  
  @Test
  void factorialProperties() {
    for (int i = 1; i < 13; i++) {
      int previous = MoreMath.factorial(i - 1);
      int current = MoreMath.factorial(i);
      assertEquals(0, current % previous);
      assertEquals(i, current / previous);
    }
  }
  
  @Test
  void intersection_handlesNull() {
    assertNull(MoreMath.intersection(null, null));
    assertEquals(
      Set.of(5, 3),
      MoreMath.intersection(Set.of(3, 5), null)
    );
    assertEquals(
      Set.of("four", "nine", "a", "tree"),
      MoreMath.intersection(null, Set.of("tree", "a", "four", "nine"))
    );
  }
}