package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.Isomorphism;

import javax.inject.Inject;

public class AutomorphismValidator implements Validator<Automorphism> {
  private final Validator<Isomorphism> isomorphismValidator;
  
  @Inject
  public AutomorphismValidator(Validator<Isomorphism> isomorphismValidator) {
    this.isomorphismValidator = isomorphismValidator;
  }
  
  @Override
  public void validate(Automorphism item) throws ValidationException {
    isomorphismValidator.validate(item);
  }
}
