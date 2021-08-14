package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class BinaryOperatorSummary {
  private final BinaryOperator binaryOperator;
  private final Map<Integer, Integer> inversesMap;
  private final Map<String, Integer> lookupMap;
  private final Set<IntCycle> cycles;
  private final String identityDisplay;
}
