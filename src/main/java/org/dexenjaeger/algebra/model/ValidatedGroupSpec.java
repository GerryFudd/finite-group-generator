package org.dexenjaeger.algebra.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class ValidatedGroupSpec {
  @Builder.Default
  private final String operatorSymbol = "*";
  @Builder.Default
  private final String identity = "I";
  @Builder.Default
  private final Map<Integer, Set<List<String>>> cyclesMap = null;
  private final Map<String, String> inversesMap;
  private final ValidatingBinaryOperator binaryOperator;
}
