package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;

public class InnerAutomorphismService {
  private final Validator<Automorphism> automorphismValidator;
  
  @Inject
  public InnerAutomorphismService(Validator<Automorphism> automorphismValidator) {
    this.automorphismValidator = automorphismValidator;
  }
  
  public Automorphism createInnerAutomorphism(Group group, int i) {
    Automorphism result = Automorphism.builder()
                            .domain(group)
                            .act(a -> group.prod(i, group.prod(a, group.getInverse(i))))
                            .build();
    try {
      automorphismValidator.validate(result);
    } catch (ValidationException e) {
      throw new RuntimeException("Result is not a valid automorphism.", e);
    }
    return result;
  }
}
