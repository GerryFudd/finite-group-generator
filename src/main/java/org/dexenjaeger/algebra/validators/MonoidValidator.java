package org.dexenjaeger.algebra.validators;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;

@RequiredArgsConstructor
public class MonoidValidator implements Validator<Monoid> {
  private final Validator<Semigroup> semigroupValidator;
  
  @Override
  public void validate(Monoid item) throws ValidationException {
    semigroupValidator.validate(item);
    for (String a: item.getElements()) {
      if (!item.prod(item.getIdentity(), a).equals(a) || !item.prod(a, item.getIdentity()).equals(a)) {
        throw new ValidationException(String.format(
          "The element %s is not an identity in this Monoid\n%s",
          item.getIdentity(), item.getMultiplicationTable()
        ));
      }
    }
  }
}
