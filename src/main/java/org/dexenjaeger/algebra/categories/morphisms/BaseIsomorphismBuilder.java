package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.function.Function;

public abstract class BaseIsomorphismBuilder<T extends Isomorphism> extends BaseHomomorphismBuilder<T> {
  @Override
  public BaseIsomorphismBuilder<T> kernel(Group kernel) {
    throw new RuntimeException("Not implemented.");
  }
  
  private Function<Integer, Integer> inverseAct;
  
  public BaseIsomorphismBuilder<T> inverseAct(Function<Integer, Integer> inverseAct) {
    this.inverseAct = inverseAct;
    return this;
  }
  
  protected int[] inverseMapping;
  
  public BaseIsomorphismBuilder<T> inverseMapping(int[] inverseMapping) {
    this.inverseMapping = inverseMapping;
    return this;
  }
}
