package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;

public interface Automorphism extends Homomorphism {
  int unApply(int b);
  
  Automorphism getInverse();
  
  @Override
  default Group getKernel() {
    return new TrivialGroup(getDomain().getOperatorSymbol(), getDomain().getIdentityDisplay());
  }
}
