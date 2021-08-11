package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

public class BinaryOperatorValidator implements Validator<BinaryOperator>{
  protected int operateSafe(BinaryOperator binaryOperator, int a, int b) throws ValidationException {
    try {
      return binaryOperator.prod(a, b);
    } catch (RuntimeException e) {
      throw new ValidationException(String.format(
        "Binary operator with size %d doesn't include (%d, %d) in its domain",
        binaryOperator.getSize(), a, b
      ));
    }
  }
  
  protected int evalSafe(BinaryOperator binaryOperator, String a) throws ValidationException {
    try {
      return binaryOperator.eval(a);
    } catch (RuntimeException e) {
      throw new ValidationException(String.format(
        "Display value %s isn't evaluated by binary operator with elements %s",
        a, binaryOperator.getElementsDisplay()
      ));
    }
  }
  
  @Override
  public void validate(BinaryOperator item) throws ValidationException {
    for (String a:item.getElementsDisplay()) {
      int i = evalSafe(item, a);
      for (String b:item.getElementsDisplay()) {
        int j = evalSafe(item, b);
        operateSafe(item, i, j);
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
