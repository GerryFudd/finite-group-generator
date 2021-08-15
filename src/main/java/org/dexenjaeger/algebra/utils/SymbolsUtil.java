package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymbolsUtil {
  private int procedurallyGenerated = 0;
  private final Map<String, Integer> usageCount = new HashMap<>();
  
  public String getNextSymbol() {
    return getSymbolForNumber(procedurallyGenerated++);
  }
  
  public Mapping applySymbol(Mapping mapping) {
    String symbol = getNextSymbol();
    usageCount.put(symbol, 1);
    mapping.setDisplay(symbol);
    return mapping;
  }
  
  public Mapping applySymbol(Mapping mapping, String symbol) {
    Optional.ofNullable(usageCount.computeIfPresent(
      symbol, (key, count) -> count + 1
      )).ifPresentOrElse(
        count -> mapping.setDisplay(String.format("%s%d", symbol, count)),
        () -> {
          mapping.setDisplay(symbol);
          usageCount.put(symbol, 1);
        }
      );
    return mapping;
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
}
