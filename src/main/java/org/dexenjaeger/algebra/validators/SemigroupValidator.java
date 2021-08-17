package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

import javax.inject.Inject;

public class SemigroupValidator implements Validator<Semigroup> {
  private final Validator<BinaryOperator> binaryOperatorValidator;
  
  @Inject
  public SemigroupValidator(Validator<BinaryOperator> binaryOperatorValidator) {
    this.binaryOperatorValidator = binaryOperatorValidator;
  }
  
  @Override
  public void validate(Semigroup item) {
    binaryOperatorValidator.validate(item);
    for (String a:item.getElementsDisplay()) {
      for (String b:item.getElementsDisplay()) {
        for (String c:item.getElementsDisplay()) {
          if (
            !item.prod(item.prod(a, b), c).equals(item.prod(a, item.prod(b, c)))
          ) {
            throw new ValidationException(String.format(
              "Binary operator is not associative (%s, %s, %s)\n%s",
              a, b, c, item.printMultiplicationTable()
            ));
          }
        }
      }
    }
  }
}
