package org.dexenjaeger.algebra.categories.morphisms;

public class IsomorphismBuilder extends BaseIsomorphismBuilder<Isomorphism> {
  
  @Override
  public Isomorphism build() {
    return new ConcreteIsomorphism(
      domain, range, mapping,
      image, inverseMapping
    );
  }
}
