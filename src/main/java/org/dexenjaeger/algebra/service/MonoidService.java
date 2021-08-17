package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.function.BiFunction;

public class MonoidService {
  private final Validator<Monoid> monoidValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  
  @Inject
  public MonoidService(
    Validator<Monoid> monoidValidator,
    BinaryOperatorUtil binaryOperatorUtil
  ) {
    this.monoidValidator = monoidValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
  }
  
  public Monoid createMonoid(
    int identity, String[] elements,
    BiFunction<Integer, Integer, Integer> operator
    ) {
    Monoid result = Monoid.builder()
      .identity(identity)
      .elements(elements)
      .size(elements.length)
      .multiplicationTable(binaryOperatorUtil.getMultiplicationTable(
        elements.length, operator
      ))
      .lookup(binaryOperatorUtil.createLookup(elements))
      .build();
    monoidValidator.validate(result);
    return result;
  }
}
