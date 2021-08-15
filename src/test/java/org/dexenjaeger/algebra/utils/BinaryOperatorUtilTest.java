package org.dexenjaeger.algebra.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryOperatorUtilTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final BinaryOperatorUtil binaryOperatorUtil = injector.getInstance(BinaryOperatorUtil.class);
  
  @Test
  void getCycleTest() {
    String[] elements = {"a", "b", "c"};
  
    BiFunction<String, String, String> operator = binaryOperatorUtil.createOperator(
      elements, (a, b) -> (a + b) % 3
    );
    
    assertEquals(
      List.of("b", "c", "a"),
      binaryOperatorUtil.getCycle("b", operator)
    );
    
    assertEquals(
      List.of("c", "b", "a"),
      binaryOperatorUtil.getCycle("c", operator)
    );
    
    assertEquals(
      List.of("a"),
      binaryOperatorUtil.getCycle("a", operator)
    );
  }
}