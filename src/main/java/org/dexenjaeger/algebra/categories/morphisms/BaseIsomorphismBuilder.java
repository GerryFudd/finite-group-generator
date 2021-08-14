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
  
  private int[] inverseMapping;
  
  public BaseIsomorphismBuilder<T> inverseMapping(int[] inverseMapping) {
    this.inverseMapping = inverseMapping;
    return this;
  }
  
  protected String[] resolveImage() {
    if (image != null) {
      return image;
    }
    image = new String[domain.getSize()];
    
    for (int i = 0; i < domain.getSize(); i++) {
      image[i] = range.display(mapping[i]);
    }
    
    return image;
  }
  
  protected int[] resolveInverseMapping() {
    if (inverseMapping != null) {
      return inverseMapping;
    }
    inverseMapping = new int[range.getSize()];
    
    for (int i = 0; i < range.getSize(); i++) {
      if (inverseAct == null) {
        inverseMapping[mapping[i]] = i;
      } else {
        inverseMapping[i] = inverseAct.apply(i);
      }
    }
    
    return inverseMapping;
  }
}
