package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.utils.Builder;

public abstract class BaseHomomorphismBuilder<T> implements Builder<T> {
  protected Group domain;
  
  public BaseHomomorphismBuilder<T> domain(Group domain) {
    this.domain = domain;
    return this;
  }
  
  protected Group range;
  
  public BaseHomomorphismBuilder<T> range(Group range) {
    this.range = range;
    return this;
  }
  
  protected Group kernel;
  
  public BaseHomomorphismBuilder<T> kernel(Group kernel) {
    this.kernel = kernel;
    return this;
  }
  
  protected int[] mapping;
  
  public BaseHomomorphismBuilder<T> mapping(int[] mapping) {
    this.mapping = mapping;
    return this;
  }
  
  protected Element[] image;
  
  public BaseHomomorphismBuilder<T> image(Element[] image) {
    this.image = image;
    return this;
  }
}
