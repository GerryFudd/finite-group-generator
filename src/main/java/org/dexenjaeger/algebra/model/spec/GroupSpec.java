package org.dexenjaeger.algebra.model.spec;

import lombok.Getter;
import lombok.Setter;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Getter
@Setter
public class GroupSpec {
  private OperatorSymbol operatorSymbol = OperatorSymbol.DEFAULT;
  private int identity;
  private Element[] elements;
  private Map<Element, Integer> lookup;
  private Map<Integer, Integer> inversesMap;
  private Set<IntCycle> maximalCycles;
  private BiFunction<Integer, Integer, Integer> operator;
}
