package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.utils.PermutationUtil;

import java.util.Iterator;

public class PermutationIterator implements Iterator<Mapping> {
  private final int totalDimensions;
  private final Iterator<Mapping> previousIterator;
  
  private Mapping currentMapping;
  private int currentIndex = 0;
  
  private PermutationIterator(int totalDimensions, Iterator<Mapping> previousIterator) {
    this.totalDimensions = totalDimensions;
    this.previousIterator = previousIterator;
  }
  
  public static PermutationIterator init(int totalDimensions, int selectedDimensions) {
    return new PermutationIterator(
      totalDimensions,
      selectedDimensions <= 1 ? new TrivialPermutationIterator() : init(totalDimensions - 1, selectedDimensions - 1)
    );
  }
  
  @Override
  public boolean hasNext() {
    return currentIndex < totalDimensions || previousIterator.hasNext();
  }
  
  @Override
  public Mapping next() {
    if (currentMapping == null || currentIndex == totalDimensions) {
      currentMapping = previousIterator.next();
      currentIndex = 0;
    }
    
    return PermutationUtil.getNextPermutation(currentMapping, currentIndex++);
  }
}
