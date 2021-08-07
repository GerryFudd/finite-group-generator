package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderedPair<T, U> {
  private final T left;
  private final U right;
}
