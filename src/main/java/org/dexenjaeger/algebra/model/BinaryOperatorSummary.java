package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class BinaryOperatorSummary {
  private final ValidatingBinaryOperator binaryOperator;
  private final Map<String, String> inverseMap;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  private final String identity;
}
