package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;

public class AutomorphismService {
  private final Validator<Automorphism> automorphismValidator;
  
  @Inject
  public AutomorphismService(Validator<Automorphism> automorphismValidator) {
    this.automorphismValidator = automorphismValidator;
  }
  
  public Automorphism compose(Automorphism a, Automorphism b) throws ValidationException {
    if (!a.getDomain().equals(b.getDomain())) {
      throw new RuntimeException("No.");
    }
    Automorphism result = Automorphism.builder()
      .domain(b.getDomain())
      .act(i -> a.apply(b.apply(i)))
      .imageFunc(i -> b.getDomain().display(a.apply(b.apply(i))))
      .build();
    automorphismValidator.validate(result);
    return result;
  }
}
