package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class BinaryOperator {
  @Getter
  private final String[] elements;
  private final Map<String, Integer> reverseLookup;
  private final BiFunction<Integer, Integer, Integer> binaryOperator;
  
  public String prod(String a, String b) {
    return elements[binaryOperator
               .apply(reverseLookup.get(a),reverseLookup.get(b))];
  }
  
  public boolean isValid() {
    if (elements.length != reverseLookup.entrySet().size()) {
      return false;
    }
    
    for (int i = 0; i < elements.length; i++) {
      if (i != reverseLookup.get(elements[i])) {
        return false;
      }
      for (int j = 0; j < elements.length; j++) {
        int product = binaryOperator.apply(i, j);
        if (product < 0 || elements.length <= product) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  public boolean isAssociative() {
    for (int i = 0; i < elements.length; i++) {
      for (int j = 0; j < elements.length; j++) {
        for (int k = 0; k < elements.length; k++) {
          if (
            !binaryOperator.apply(binaryOperator.apply(i, j), k).equals(binaryOperator.apply(i, binaryOperator.apply(j, k)))
          ) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
