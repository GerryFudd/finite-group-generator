package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

public abstract class BaseIsomorphismBuilder<T extends Isomorphism> extends BaseHomomorphismBuilder<T> {
  @Override
  public BaseIsomorphismBuilder<T> kernel(Group kernel) {
    throw new RuntimeException("Not implemented.");
  }
  
  protected int[] inverseMapping;
  
  public BaseIsomorphismBuilder<T> inverseMapping(int[] inverseMapping) {
    this.inverseMapping = inverseMapping;
    return this;
  }
}
