package org.dexenjaeger.algebra.model;

import java.util.Iterator;

public class TrivialPermutationIterator implements Iterator<Mapping> {
  private final Mapping initialMapping = new Mapping(new int[]{});
  private boolean hasNext = true;
  @Override
  public boolean hasNext() {
    return hasNext;
  }
  
  @Override
  public Mapping next() {
    hasNext = false;
    return initialMapping;
  }
}
