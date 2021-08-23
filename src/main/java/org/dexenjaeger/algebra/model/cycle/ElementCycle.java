package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ElementCycle extends AbstractCycle<Element, ElementCycle> {
  
  public ElementCycle(
    int size, Element[] elements, Map<Element, Integer> lookup,
    int[] generators, Map<Integer, Integer> subCycleGenerators,
    Function<List<Element>, ElementCycle> make
    ) {
    super(
      size, elements, lookup, generators, subCycleGenerators,
      make
    );
  }
  
  public static ElementCycleBuilder builder() {
    return new ElementCycleBuilder();
  }
  
  @Override
  public String toString() {
    return new StringBuilder("(")
      .append(Stream.of(elements).map(Element::toString).collect(Collectors.joining()))
      .append(")")
      .toString();
  }
}
