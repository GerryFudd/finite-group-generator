package org.dexenjaeger.algebra.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "0,0:0", "1,1:1", "2,3:1", "3,8:1",
      "4,6:2", "24,36:12", "1820,3315:65"
    },
    delimiter = ':'
  )
  void gcdTest(String a, String b) {
    List<Integer> inputs = Stream.of(a.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    assertEquals(
      Integer.parseInt(b),
      MoreMath.gcd(
        inputs.get(0), inputs.get(1)
      )
    );
  }
}