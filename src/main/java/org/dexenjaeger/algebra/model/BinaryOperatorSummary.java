package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Getter
@Builder
public class BinaryOperatorSummary {
  private final String[] elements;
  private final BiFunction<Integer, Integer, Integer> operator;
  private final Map<Integer, Integer> inversesMap;
  private final Map<String, Integer> lookupMap;
  private final Set<IntCycle> cycles;
  private final String identityDisplay;
}
