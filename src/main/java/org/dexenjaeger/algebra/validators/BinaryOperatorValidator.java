package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

public class BinaryOperatorValidator implements Validator<BinaryOperator>{
  
  @Override
  public void validate(BinaryOperator item) {
    for (Element a:item.getElementsDisplay()) {
      for (Element b:item.getElementsDisplay()) {
        try {
          item.prod(a, b);
        } catch (RuntimeException e) {
          throw new ValidationException(String.format(
            "The product of %s and %s doesn't exist in this binary operator\n%s",
            a, b, item.printMultiplicationTable()
          ));
        }
      }
    }
  }
}
