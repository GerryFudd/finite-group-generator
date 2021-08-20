package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;

import javax.inject.Inject;

public class MonoidValidator implements Validator<Monoid> {
  private final Validator<Semigroup> semigroupValidator;
  
  @Inject
  public MonoidValidator(Validator<Semigroup> semigroupValidator) {
    this.semigroupValidator = semigroupValidator;
  }
  
  @Override
  public void validate(Monoid item) {
    semigroupValidator.validate(item);
    for (int i = 0; i < item.getSize(); i++) {
      if (item.prod(item.getIdentity(), i) != i || item.prod(i, item.getIdentity()) != i) {
        throw new ValidationException(String.format(
          "The element %s is not an identity in this Monoid\n%s",
          item.getIdentityDisplay(), item.printMultiplicationTable()
        ));
      }
    }
  }
}
