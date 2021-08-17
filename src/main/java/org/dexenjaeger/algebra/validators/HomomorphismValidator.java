package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;

public class HomomorphismValidator implements Validator<Homomorphism> {
  private ValidationException getNotFunctionException(
    int n, int fi, int i) {
    return new ValidationException(String.format(
      "Range with size %d doesn't contain image %d of %d.",
      n, fi, i
    ));
  }
  
  private void validateHomomorphism(Homomorphism item) {
    int rangeSize = item.getRange().getSize();
    for (int i = 0; i < item.getDomain().getSize(); i++) {
      int fi = item.apply(i);
      if (rangeSize <= fi) {
        throw getNotFunctionException(rangeSize, fi, i);
      }
      for (int j = 0; j < item.getDomain().getSize(); j++) {
        int fj = item.apply(j);
        if (rangeSize <= fj) {
          throw getNotFunctionException(rangeSize, fj, j);
        }
        if (item.getRange().prod(fi, fj)
              != item.apply(item.getDomain().prod(i, j))
        ) {
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
  
  private void validateInverseImageOfId(Homomorphism item) {
    for (String a:item.getDomain().getElementsDisplay()) {
      if (item.getKernel().getElementsDisplay().contains(a)
            != item.getRange()
                 .display(item.apply(item.getDomain().eval(a)))
                 .equals(item.getRange().getIdentityDisplay())) {
        throw new ValidationException("Kernel is not the inverse image of the identity.");
      }
    }
  }
  
  public static void validateSubgroup(Homomorphism item) {
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
  public void validate(Homomorphism item) {
    validateHomomorphism(item);
    validateInverseImageOfId(item);
    validateSubgroup(item);
  }
}
