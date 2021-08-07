package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

import java.util.Map;

@Getter
@Builder
public class BinaryOperatorSummary {
  private final ValidatingBinaryOperator binaryOperator;
  private final Map<String, String> inverseMap;
  private final String identity;
}
