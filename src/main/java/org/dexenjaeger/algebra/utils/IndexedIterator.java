package org.dexenjaeger.algebra.utils;

import java.util.Iterator;
import java.util.function.Function;

public class IndexedIterator<T> implements Iterator<T> {
  private final int stop;
  private final Function<Integer, T> indexedFunction;
  
  private int currentIndex = 0;
  
  public IndexedIterator(int stop, Function<Integer, T> indexedFunction) {
    this.stop = stop;
    this.indexedFunction = indexedFunction;
  }
  
  @Override
  public boolean hasNext() {
    return currentIndex < stop;
  }
  
  @Override
  public T next() {
    return indexedFunction.apply(currentIndex++);
  }
}
