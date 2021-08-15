package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class StringCycle extends AbstractCycle<String, StringCycle> {
  
  public StringCycle(
    int size, String[] elements, Map<String, Integer> lookup,
    int[] generators, Map<Integer, Integer> subCycleGenerators,
    Function<List<String>, StringCycle> make
    ) {
    super(
      size, elements, lookup, generators, subCycleGenerators,
      make
    );
  }
  
  public static StringCycleBuilder builder() {
    return new StringCycleBuilder();
  }
  
  @Override
  public String toString() {
    return new StringBuilder("(")
      .append(String.join("", elements))
      .append(")")
      .toString();
  }
}
