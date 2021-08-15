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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
class MoreMathTest {
  @Test
  void factorialInvalidInput() {
    assertThrows(RuntimeException.class, () -> MoreMath.factorial(-1));
  }
  
  @Test
  void factorialOverflow() {
    int THIRTEEN = MoreMath.factorial(13);
    assertEquals(
      1932053504,
      THIRTEEN
    );
    assertThrows(RuntimeException.class, () -> MoreMath.factorial(14));
    assertTrue(
      Integer.MAX_VALUE / 14 < THIRTEEN,
      "13! is greater than 1/14th max int"
    );
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
  
  @Test
  void gcd_invalidInput() {
    RuntimeException e1 = assertThrows(
      RuntimeException.class,
      () -> MoreMath.gcd(-1, 3)
    );
    RuntimeException e2 = assertThrows(
      RuntimeException.class,
      () -> MoreMath.gcd(5, -3)
    );
    
    assertEquals(
      "This method is only implemented for positive inputs.",
      e1.getMessage()
    );
    assertEquals(
      e1.getMessage(),
      e2.getMessage()
    );
  }
  
  @Test
  void pow_invalidBaseInput() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> MoreMath.pow(-1, 5)
    );
    
    assertEquals(
      "Exponential expressions may not have negative bases.",
      e.getMessage()
    );
  }
  
  @Test
  void pow_invalidExponentInput() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> MoreMath.pow(2, -3)
    );
    
    assertEquals(
      "Negative exponents are not defined for natural number bases above one.",
      e.getMessage()
    );
  }
  
  @Test
  void pow_zeroOrOneBaseWithNegative() {
    assertEquals(
      0, MoreMath.pow(0, -8)
    );
    assertEquals(
      1, MoreMath.pow(1, -8)
    );
  }
  
  @Test
  void pow_basicCases() {
    assertEquals(
      27,
      MoreMath.pow(3, 3)
    );
    assertEquals(
      32,
      MoreMath.pow(2, 5)
    );
    assertEquals(
      125,
      MoreMath.pow(5, 3)
    );
  }
  
  @Test
  void pow_overflowException() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> MoreMath.pow(13, 12)
    );
    
    assertEquals(
      "Exponent 13^12 overflowed max int 2147483647.", e.getMessage()
    );
    
    int previous = MoreMath.pow(13, 11);
    assertTrue(
      previous < Integer.MAX_VALUE,
      "The previous exponent is less than max."
    );
    assertTrue(
      previous > Integer.MAX_VALUE / 13,
      "The previous exponent is more than 1/13th of the max value."
    );
  }
}