package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.Group;
import org.dexenjaeger.algebra.categories.objects.TrivialGroup;
import org.dexenjaeger.algebra.categories.objects.UnsafeGroup;

import java.util.Map;

public interface Automorphism extends Homomorphism {
  Automorphism getInverse();
  
  @Override
  default Group getKernel() {
    return new TrivialGroup(getDomain().getOperatorSymbol(), getDomain().getIdentity());
  }
}
