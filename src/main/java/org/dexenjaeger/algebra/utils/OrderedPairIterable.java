package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Iterator;

public class OrderedPairIterable<T, U> implements Iterable<OrderedPair<T, U>> {
  private final Iterable<T> leftIterable;
  private final Iterable<U> rightIterable;
  
  public OrderedPairIterable(Iterable<T> leftIterable, Iterable<U> rightIterable) {
    this.leftIterable = leftIterable;
    this.rightIterable = rightIterable;
  }
  
  @Override
  public Iterator<OrderedPair<T, U>> iterator() {
    return new OrderedPairIterator<>(leftIterable, rightIterable.iterator());
  }
}
