package org.dexenjaeger.algebra.validators;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

@RequiredArgsConstructor
public class SemigroupValidator implements Validator<Semigroup> {
  private final Validator<BinaryOperator> binaryOperatorValidator;
  
  @Override
  public void validate(Semigroup item) throws ValidationException {
    binaryOperatorValidator.validate(item);
    for (String a:item.getElements()) {
      for (String b:item.getElements()) {
        for (String c:item.getElements()) {
          if (
            !item.prod(item.prod(a, b), c).equals(item.prod(a, item.prod(b, c)))
          ) {
            throw new ValidationException(String.format(
              "Binary operator is not associative\n%s",
              item.getMultiplicationTable()
            ));
          }
        }
      }
    }
  }
}
