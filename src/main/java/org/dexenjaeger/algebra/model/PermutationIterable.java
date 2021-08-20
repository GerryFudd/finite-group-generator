package org.dexenjaeger.algebra.model;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor
public class PermutationIterable implements Iterable<Mapping> {
  private final int totalDimensions;
  private final int selectedDimensions;
  @Override
  public Iterator<Mapping> iterator() {
    return PermutationIterator.init(totalDimensions, selectedDimensions);
  }
}
