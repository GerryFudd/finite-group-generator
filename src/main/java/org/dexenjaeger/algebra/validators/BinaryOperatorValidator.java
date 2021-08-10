package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

public class BinaryOperatorValidator implements Validator<BinaryOperator>{
  @Override
  public void validate(BinaryOperator item) throws ValidationException {
    for (String a:item.getElementsDisplay()) {
      for (String b:item.getElementsDisplay()) {
        try {
          item.prod(a, b);
        } catch (RuntimeException e) {
          throw new ValidationException(String.format(
            "This set is not closed under %s\n%s",
            item.getOperatorSymbol(), item.printMultiplicationTable()
          ));
        }
      }
    }
  }
}
