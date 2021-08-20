package org.dexenjaeger.algebra.utils;

import java.util.Iterator;
import java.util.function.Function;

public class IndexedIterable<T> implements Iterable<T> {
  private final int stop;
  private final Function<Integer, T> indexedFunction;
  
  public IndexedIterable(int stop, Function<Integer, T> indexedFunction) {
    this.stop = stop;
    this.indexedFunction = indexedFunction;
  }
  
  @Override
  public Iterator<T> iterator() {
    return new IndexedIterator<>(stop, indexedFunction);
  }
}
