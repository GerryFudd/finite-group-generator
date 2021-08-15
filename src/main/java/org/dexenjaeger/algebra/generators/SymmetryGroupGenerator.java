package org.dexenjaeger.algebra.generators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.service.GroupService;
import org.dexenjaeger.algebra.utils.MoreMath;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class SymmetryGroupGenerator {
  private final Validator<Group> groupValidator;
  private final BinaryOperatorService binaryOperatorService;
  private final GroupService groupService;
  
  @Inject
  public SymmetryGroupGenerator(
    Validator<Group> groupValidator, BinaryOperatorService binaryOperatorService,
    GroupService groupService
  ) {
    this.groupValidator = groupValidator;
    this.binaryOperatorService = binaryOperatorService;
    this.groupService = groupService;
  }
  
  private int[][] getPermutationSet(int n) {
    int[][] result = new int[MoreMath.factorial(n)][n];
    if (n < 1) {
      throw new RuntimeException("No.");
    }
    
    if (n == 1) {
      result[0] = new int[1];
      return result;
    }
    
    int[][] previousPermutations = getPermutationSet(n - 1);
    for (int i = 0; i < previousPermutations.length; i++) {
      int[] previousPermutation = previousPermutations[i];
      for (int j = 0; j < n; j++) {
        int[] newPermutation = new int[n];
        for (int k = 0; k < n; k++) {
          if (k == j) {
            newPermutation[k] = 0;
          } else if (k < j) {
            newPermutation[k] = previousPermutation[k] + 1;
          } else {
            newPermutation[k] = previousPermutation[k - 1] + 1;
          }
        }
        result[j * previousPermutations.length + i] = newPermutation;
      }
    }
    return result;
  }
  
  private String getPermutationAsString(int[] permutation) {
    StringBuilder sb = new StringBuilder();
    for (int x : permutation) {
      sb.append(x);
    }
    return sb.toString();
  }
  
  private String multiplyPermutations(int[] p1, int[] p2) {
    int[] prod = new int[p1.length];
    for (int i = 0; i < p1.length; i++) {
      prod[i] = p1[p2[i]];
    }
    return getPermutationAsString(prod);
  }
  
  public Group createSymmetryGroup(int n) {
    int[][] permutations = getPermutationSet(n);
    Map<String, Integer> reverseLookup = new HashMap<>();
    for (int i = 0; i < permutations.length; i++) {
      reverseLookup.put(
        getPermutationAsString(permutations[i]),
        i
      );
    }
    
    int[][] binOp = new int[permutations.length][permutations.length];
    for (int i = 0; i < permutations.length; i++) {
      for (int j = 0; j < permutations.length; j++) {
        binOp[i][j] = reverseLookup.get(multiplyPermutations(
          permutations[i],
          permutations[j]
        ));
      }
    }
  
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
        permutations.length,
        (a,b) -> binOp[a][b]
      );
  
    try {
      return groupService.createGroup(
        "o",
        0,
        summary.getElements(),
        summary.getInversesMap(),
        summary.getCycles(),
        summary.getOperator()
      );
    } catch (ValidationException e) {
      throw new RuntimeException(
        "Generated group didn't validate", e
      );
    }
  }
}
