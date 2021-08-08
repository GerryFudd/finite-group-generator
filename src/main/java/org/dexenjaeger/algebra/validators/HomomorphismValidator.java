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
    Set<String> rangeElements = item.getRange().getElements();
    for (String a:item.getDomain().getElements()) {
      String fa = item.apply(a);
      if (!rangeElements.contains(fa)) {
        throw getNotFunctionException(rangeElements, item.getRange().getIdentity(), fa, a);
      }
      for (String b:item.getDomain().getElements()) {
        String fb = item.apply(b);
        if (!rangeElements.contains(fb)) {
          throw getNotFunctionException(rangeElements, item.getRange().getIdentity(), fb, b);
        }
        if (!rangeElements.contains(item.getRange().prod(fa, fb))) {
          throw new RuntimeException(String.format(
            "Range %s isn't closed under %s.",
            String.join(", ", rangeElements), item.getRange().getOperatorSymbol()
          ));
        }
        if (!item.getRange().prod(item.apply(a), item.apply(b))
               .equals(item.apply(item.getDomain().prod(a, b)))) {
          throw new RuntimeException("Function is not a homomorphism.");
        }
      }
    }
  }
  
  private void validateInverseImageOfId(Homomorphism item) {
    for (String a:item.getDomain().getElements()) {
      if (item.getKernel().getElements().contains(a) != item.apply(a).equals(item.getRange().getIdentity())) {
        throw new RuntimeException("Kernel is not the inverse image of the identity.");
      }
    }
  }
  
  
  
  public static void validateSubgroup(Homomorphism item) throws ValidationException {
    for (String a:item.getKernel().getElements()) {
      for (String b:item.getKernel().getElements()) {
        String c = item.getDomain().prod(a, b);
        if (!item.getKernel().getElements().contains(c)) {
          throw new ValidationException("Kernel is not closed under the group operation.");
        }
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
