package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.function.BiFunction;

public class SemigroupService {
  private final Validator<Semigroup> semigroupValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  
  @Inject
  public SemigroupService(
    Validator<Semigroup> semigroupValidator,
    BinaryOperatorUtil binaryOperatorUtil
  ) {
    this.semigroupValidator = semigroupValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
  }
  
  public Semigroup createSemigroup(
    String[] elements, BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    return createSemigroup("*", elements, operator);
  }
  
  public Semigroup createSemigroup(
    String operatorSymbol, String[] elements, BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    Semigroup result = Semigroup.builder()
                         .operatorSymbol(operatorSymbol)
                         .elements(elements)
                         .lookup(binaryOperatorUtil.createLookup(elements))
                         .size(elements.length)
                         .multiplicationTable(binaryOperatorUtil.getMultiplicationTable(
                           elements.length, operator
                         ))
                         .build();
    semigroupValidator.validate(result);
    return result;
  }
}
