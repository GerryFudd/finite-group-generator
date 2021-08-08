package org.dexenjaeger.algebra.categories.objects.monoid;

import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;

public interface Monoid extends Semigroup {
  String getIdentity();
}
