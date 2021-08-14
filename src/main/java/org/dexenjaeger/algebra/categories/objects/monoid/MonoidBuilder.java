package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MonoidBuilder extends BaseMonoidBuilder<Monoid> {
  @Override
  public Monoid build() {
    return new ConcreteMonoid(
      operatorSymbol, resolveSize(), resolveElements(),
      identity, resolveLookup(), resolveMultiplicationTable()
    );
  }
}
