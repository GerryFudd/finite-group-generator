package org.dexenjaeger.algebra.categories.objects.monoid;

import org.dexenjaeger.algebra.model.binaryoperator.BaseBinaryOperatorBuilder;

public abstract class BaseMonoidBuilder<T extends Monoid> extends BaseBinaryOperatorBuilder<T> {
  protected int identity = 0;
  
  public BaseMonoidBuilder<T> identity(int identity) {
    this.identity = identity;
    return this;
  }
}
