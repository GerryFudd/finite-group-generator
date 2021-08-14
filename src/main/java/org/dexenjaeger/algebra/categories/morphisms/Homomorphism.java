package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

public interface Homomorphism {
  Group getDomain();
  Group getRange();
  Group getKernel();
  int apply(int in);
  
  static HomomorphismBuilder builder() {
    return new HomomorphismBuilder();
  }
}
