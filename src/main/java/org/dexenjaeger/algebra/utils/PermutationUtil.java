package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;

import java.util.ArrayList;
import java.util.List;

public class PermutationUtil {
  public static List<Mapping> getPermutationList(int n) {
    if (n == 1) {
      return List.of(new Mapping(new int[]{0}));
    }
    List<Mapping> result = new ArrayList<>(MoreMath.factorial(n));
    
    for (Mapping previousPermutation : getPermutationList(n - 1)) {
      for (int i = 0; i < n; i++) {
        int[] newPermutation = new int[n];
        for (int j = 0; j < n; j++) {
          if (j == i) {
            newPermutation[j] = 0;
          } else if (j < i) {
            newPermutation[j] = previousPermutation.get(j) + 1;
          } else {
            newPermutation[j] = previousPermutation.get(j - 1) + 1;
          }
        }
        result.add(new Mapping(newPermutation));
      }
    }
    return result;
  }
}
