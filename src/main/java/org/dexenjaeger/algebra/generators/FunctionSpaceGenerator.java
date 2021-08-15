package org.dexenjaeger.algebra.generators;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.service.MonoidService;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.MoreMath;
import org.dexenjaeger.algebra.validators.ValidationException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FunctionSpaceGenerator {
  private final MonoidService monoidService;
  private final BinaryOperatorService binaryOperatorService;
  private final FunctionsUtil functionsUtil;
  
  @Inject
  public FunctionSpaceGenerator(
    MonoidService monoidService,
    BinaryOperatorService binaryOperatorService,
    FunctionsUtil functionsUtil
  ) {
    this.monoidService = monoidService;
    this.binaryOperatorService = binaryOperatorService;
    this.functionsUtil = functionsUtil;
  }
  private List<Mapping> createFunctionSet(int n) {
    int size = MoreMath.pow(n ,n);
    List<Mapping> functions = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      int placeMultiplier = 1;
      int[] newMapping = new int[n];
      for (int j = n - 1; 0 <= j; j--) {
        newMapping[j] = (i / placeMultiplier) % n;
        placeMultiplier *= n;
      }
      functions.add(new Mapping(newMapping));
    }
    return functions;
  }
  public Monoid createFunctionSpace(int n) throws ValidationException {
    List<Mapping> functions = createFunctionSet(n);
    
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      functions.size(),
      functionsUtil.createBinaryOperatorForFunctionSpace(functions)
    );
    
    return monoidService.createMonoid(
      0, summary.getElements(), summary.getOperator()
    );
  }
}
