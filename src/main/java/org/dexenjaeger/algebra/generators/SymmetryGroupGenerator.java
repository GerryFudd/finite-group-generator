package org.dexenjaeger.algebra.generators;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.service.GroupService;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.PermutationUtil;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;

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
  
  public Group createSymmetryGroup(int n) {
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      PermutationUtil.getPermutationList(n)
    );
  
    try {
      return groupService.createGroup(
        new GroupSpec()
        .setOperatorSymbol("o")
        .setIdentity(0)
        .setElements(summary.getElements())
        .setMaximalCycles(summary.getCycles())
        .setOperator(summary.getOperator())
      );
    } catch (ValidationException e) {
      throw new RuntimeException(
        "Generated group didn't validate", e
      );
    }
  }
}
