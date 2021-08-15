package org.dexenjaeger.algebra.model.spec;

import lombok.Getter;
import lombok.Setter;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Getter
@Setter
public class GroupSpec {
  private String operatorSymbol = "*";
  private int identity;
  private String[] elements;
  private Map<String, Integer> lookup;
  private Map<Integer, Integer> inversesMap;
  private Set<IntCycle> maximalCycles;
  private BiFunction<Integer, Integer, Integer> operator;
}
