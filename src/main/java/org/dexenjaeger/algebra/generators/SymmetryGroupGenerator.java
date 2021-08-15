package org.dexenjaeger.algebra.generators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.service.GroupService;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.MoreMath;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SymmetryGroupGenerator {
  private final FunctionsUtil functionsUtil;
  private final BinaryOperatorService binaryOperatorService;
  private final GroupService groupService;
  
  @Inject
  public SymmetryGroupGenerator(
    FunctionsUtil functionsUtil, BinaryOperatorService binaryOperatorService,
    GroupService groupService
  ) {
    this.functionsUtil = functionsUtil;
    this.binaryOperatorService = binaryOperatorService;
    this.groupService = groupService;
  }
  
  private List<Mapping> getPermutationList(int n) {
    if (n == 1) {
      return List.of(new Mapping(new int[]{0}));
    }
    List<Mapping> result = new ArrayList<>(MoreMath.factorial(n));
    
    for (Mapping previousPermutation : getPermutationList(n - 1)) {
      for (int j = 0; j < n; j++) {
        int[] newPermutation = new int[n];
        for (int k = 0; k < n; k++) {
          if (k == j) {
            newPermutation[k] = 0;
          } else if (k < j) {
            newPermutation[k] = previousPermutation.get(k) + 1;
          } else {
            newPermutation[k] = previousPermutation.get(k - 1) + 1;
          }
        }
        result.add(new Mapping(newPermutation));
      }
    }
    return result;
  }
  
  public Group createSymmetryGroup(int n) {
    List<Mapping> permutations = getPermutationList(n);
  
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
        permutations.size(),
        functionsUtil.createBinaryOperatorForFunctionSpace(permutations)
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
