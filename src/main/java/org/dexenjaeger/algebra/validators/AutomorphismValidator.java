package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;

import javax.inject.Inject;

public class AutomorphismValidator implements Validator<Automorphism> {
  private final Validator<Homomorphism> homomorphismValidator;
  
  @Inject
  public AutomorphismValidator(Validator<Homomorphism> homomorphismValidator) {
    this.homomorphismValidator = homomorphismValidator;
  }
  
  @Override
  public void validate(Automorphism item) throws ValidationException {
    homomorphismValidator.validate(item);
    
    // Check that the automorphism is injective.
    if (item.getKernel().getElements().size() > 1) {
      throw new ValidationException("The kernel of an automorphism must be trivial.");
    }
    // This Automorphism has already been validated as
    // a function and the function is an injection. If the
    // domain and range are the same size, this is a
    // bijection.
    if (item.getDomain().getElements().size()
          != item.getRange().getElements().size()) {
      throw new ValidationException("Domain and range are different sizes.");
    }
    
    // If the reverse map is a left inverse, it is also an inverse
    // function.
    for (String x:item.getDomain().getElements()) {
      if (!x.equals(item.unApply(item.apply(x)))) {
        throw new ValidationException("Function is not a left inverse.");
      }
    }
  }
}
