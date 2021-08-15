package org.dexenjaeger.algebra.categories.morphisms;

public class HomomorphismBuilder extends BaseHomomorphismBuilder<Homomorphism> {
  
  
  @Override
  public Homomorphism build() {
    return new ConcreteHomomorphism(
      domain, range, kernel,
      mapping, image
    );
  }
}
