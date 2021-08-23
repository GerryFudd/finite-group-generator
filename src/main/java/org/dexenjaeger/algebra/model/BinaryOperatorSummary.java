package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Set;
import java.util.function.BiFunction;

@Getter
@Builder
public class BinaryOperatorSummary {
  private final Element[] elements;
  private final BiFunction<Integer, Integer, Integer> operator;
  private final Set<IntCycle> cycles;
}
