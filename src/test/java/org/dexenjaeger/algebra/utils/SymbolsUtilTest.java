package org.dexenjaeger.algebra.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolsUtilTest {
  @Test
  void applySymbol_largeSets() {
    SymbolsUtil symbolsUtil = new SymbolsUtil();
    for (int i = 0; i < 26; i++) {
      assertEquals(
        1,
        symbolsUtil.getNextSymbol().length()
        );
    }
    assertEquals("aa", symbolsUtil.getNextSymbol());
    assertEquals("ab", symbolsUtil.getNextSymbol());
  }
}