package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;

import java.util.Optional;
import java.util.function.Function;

public class HomomorphismValidator implements Validator<Homomorphism> {
  
  private static ValidationException getInvalidDomainException(
    int n, int i) {
    return new ValidationException(String.format(
      "Homomorphism not defined on domain with size %d for input %d.",
      n, i
    ));
  }
  
  private int applySafe(Function<Integer, Integer> act, int n, int i) throws ValidationException {
    try {
      return Optional.ofNullable(act.apply(i)).orElseThrow(() -> getInvalidDomainException(n, i));
    } catch (RuntimeException e) {
      throw getInvalidDomainException(n, i);
    }
  }
  
  private static ValidationException getNotFunctionException(
    int n, int fi, int i) {
    return new ValidationException(String.format(
      "Range with size %d doesn't contain image %d of %d.",
      n, fi, i
    ));
  }
  
  private void validateHomomorphism(Homomorphism item) throws ValidationException {
    int rangeSize = item.getRange().getSize();
    for (int i = 0; i < item.getDomain().getSize(); i++) {
      int fi = applySafe(item::apply, item.getDomain().getSize(), i);
      if (rangeSize <= fi) {
        throw getNotFunctionException(rangeSize, fi, i);
      }
      for (int j = 0; j < item.getDomain().getSize(); j++) {
        int fj = applySafe(item::apply, item.getDomain().getSize(), j);
        if (rangeSize <= fj) {
          throw getNotFunctionException(rangeSize, fj, j);
        }
        if (item.getRange().prod(fi, fj)
              != applySafe(
                item::apply, item.getDomain().getSize(), item.getDomain().prod(i, j)
        )) {
          throw new ValidationException(String.format(
            "Function is not a homomorphism, f(%d)%sf(%d)=%d, but f(%d%s%d)=%d.",
            i, item.getRange().getOperatorSymbol(),
            j, item.getRange().prod(fi, fj),
            i, item.getDomain().getOperatorSymbol(), j,
            item.apply(item.getDomain().prod(i, j))
          ));
        }
      }
    }
  }
  
  private void validateInverseImageOfId(Homomorphism item) throws ValidationException {
    for (String a:item.getDomain().getElementsDisplay()) {
      if (item.getKernel().getElementsDisplay().contains(a)
            != item.getRange()
                 .display(item.apply(item.getDomain().eval(a)))
                 .equals(item.getRange().getIdentityDisplay())) {
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
