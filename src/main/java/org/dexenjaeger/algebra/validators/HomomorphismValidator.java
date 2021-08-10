package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.Collection;
import java.util.Set;

public class HomomorphismValidator implements Validator<Homomorphism> {
  
  private static ValidationException getNotFunctionException(
    Collection<String> rangeElements, String identity, String output, String input) {
    return new ValidationException(String.format(
      "Range %s doesn't contain image %s of %s.",
      BinaryOperatorUtil.getSortedElements(rangeElements, identity),
      output, input
    ));
  }
  
  private void validateHomomorphism(Homomorphism item) throws ValidationException {
    Set<String> rangeElements = item.getRange().getElementsDisplay();
    for (String a:item.getDomain().getElementsDisplay()) {
      String fa = item.apply(a);
      if (!rangeElements.contains(fa)) {
        throw getNotFunctionException(rangeElements, item.getRange().getIdentityDisplay(), fa, a);
      }
      for (String b:item.getDomain().getElementsDisplay()) {
        String fb = item.apply(b);
        if (!rangeElements.contains(fb)) {
          throw getNotFunctionException(rangeElements, item.getRange().getIdentityDisplay(), fb, b);
        }
        if (!item.getRange().prod(fa, fb)
               .equals(item.apply(item.getDomain().prod(a, b)))) {
          throw new ValidationException(String.format(
            "Function is not a homomorphism, f(%s)%sf(%s)=%s, but f(%s%s%s)=%s.",
            a, item.getRange().getOperatorSymbol(),
            b, item.getRange().prod(fa, fb),
            a, item.getDomain().getOperatorSymbol(), b,
            item.apply(item.getDomain().prod(a, b))
          ));
        }
      }
    }
  }
  
  private void validateInverseImageOfId(Homomorphism item) throws ValidationException {
    for (String a:item.getDomain().getElementsDisplay()) {
      if (item.getKernel().getElementsDisplay().contains(a) != item.apply(a).equals(item.getRange().getIdentityDisplay())) {
        throw new ValidationException("Kernel is not the inverse image of the identity.");
      }
    }
  }
  
  public static void validateSubgroup(Homomorphism item) throws ValidationException {
    for (String a:item.getKernel().getElementsDisplay()) {
      for (String b:item.getKernel().getElementsDisplay()) {
        String c = item.getDomain().prod(a, b);
        if (!c.equals(item.getKernel().prod(a, b))) {
          throw new ValidationException("Kernel binary operator doesn't match group binary operator.");
        }
      }
    }
  }
  
  @Override
  public void validate(Homomorphism item) throws ValidationException {
    validateHomomorphism(item);
    validateInverseImageOfId(item);
    validateSubgroup(item);
  }
}
