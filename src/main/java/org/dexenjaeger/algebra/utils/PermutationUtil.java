package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.PermutationIterable;

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
        result.add(getNextPermutation(previousPermutation, i));
      }
    }
    return result;
  }
  
//  public static Mapping getNextPermutation(
//    Mapping previousPermutation, int currentIndex
//  ) {
//    int n = previousPermutation.size() + 1;
//    int[] newPermutation = new int[n];
//    for (int j = 0; j < n; j++) {
//      if (j == currentIndex) {
//        newPermutation[j] = 0;
//      } else if (j < currentIndex) {
//        newPermutation[j] = previousPermutation.get(j) + 1;
//      } else {
//        newPermutation[j] = previousPermutation.get(j - 1) + 1;
//      }
//    }
//    return new Mapping(newPermutation);
//  }
  
  /**
   * getNextPermutationFunction cycles through k-permutation functions P(k, n). It is
   * implemented in such a way that it is deterministic and if you invoke
   * this function once for every Mapping in P(k - 1, n - 1) and
   * 0 <= currentIndex < n, you will get every k-permutation function in P(k, n).
   * @param previousKPermutation : A representative of P(k - 1, n - 1)
   * @param currentIndex : The index of the generated k-permutation function within the list of
   *                    k-permutation functions.
   * @return A Mapping with dimension k containing distinct numbers in 0 <= i < n
   */
  public static Mapping getNextPermutation(
    Mapping previousKPermutation, int currentIndex
  ) {
    int k = previousKPermutation.size() + 1;
    int[] newKPermutation = new int[k];
    for (int i = 0; i < k - 1; i++) {
      if (currentIndex <= previousKPermutation.get(i)) {
        newKPermutation[i] = previousKPermutation.get(i) + 1;
      } else {
        newKPermutation[i] = previousKPermutation.get(i);
      }
    }
    newKPermutation[k - 1] = currentIndex;
    return new Mapping(newKPermutation);
  }
  
  public static Iterable<Mapping> getPermutationIterable(int n) {
    return new PermutationIterable(n, n);
  }
  
  public static Iterable<Mapping> getKPermutationIterable(int n, int k) {
    return new PermutationIterable(n, k);
  }
}
