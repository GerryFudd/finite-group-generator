package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

public interface Homomorphism {
  Group getDomain();
  Group getRange();
  Group getKernel();
  int apply(int in);
  Element apply(Element x);
  
  static HomomorphismBuilder builder() {
    return new HomomorphismBuilder();
  }
}
