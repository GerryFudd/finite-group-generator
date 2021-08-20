package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.OrderedPair;

import java.util.Iterator;

public class OrderedPairIterator<T, U> implements Iterator<OrderedPair<T, U>> {
  private final Iterable<T> leftIteratorProvider;
  private final Iterator<U> rightIterator;
  
  private Iterator<T> leftIterator;
  
  private U currentRight;
  
  public OrderedPairIterator(Iterable<T> leftIteratorProvider, Iterator<U> rightIterator) {
    this.leftIteratorProvider = leftIteratorProvider;
    this.rightIterator = rightIterator;
  }
  
  @Override
  public boolean hasNext() {
    if (leftIterator == null) {
      return true;
    }
    return leftIterator.hasNext() || rightIterator.hasNext();
  }
  
  @Override
  public OrderedPair<T, U> next() {
    T currentLeft;
    if (leftIterator == null) {
      leftIterator = leftIteratorProvider.iterator();
      currentLeft = leftIterator.next();
      currentRight = rightIterator.next();
      return new OrderedPair<>(currentLeft, currentRight);
    }
    
    if (leftIterator.hasNext()) {
      currentLeft = leftIterator.next();
    } else {
      leftIterator = leftIteratorProvider.iterator();
      currentLeft = leftIterator.next();
      currentRight = rightIterator.next();
    }
    return new OrderedPair<>(
      currentLeft, currentRight
    );
  }
}
