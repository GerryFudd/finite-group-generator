package org.dexenjaeger.algebra.categories.objects.monoid;

import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

public interface Monoid extends Semigroup {
  Element getIdentityDisplay();
  int getIdentity();
  
  static MonoidBuilder builder() {
    return new MonoidBuilder();
  }
}
