package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

public interface Homomorphism {
  Group getDomain();
  Group getRange();
  Group getKernel();
  String apply(String in);
}
