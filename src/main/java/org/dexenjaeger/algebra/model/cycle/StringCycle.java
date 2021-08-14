package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class StringCycle extends AbstractCycle<String, StringCycle> {
  
  public StringCycle(int size, String[] elements, Map<String, Integer> lookup, int[] generators, Map<Integer, Integer> subCycleGenerators) {
    super(
      size, elements, lookup, generators, subCycleGenerators,
      spec -> builder().elements(spec).build()
    );
  }
  
  public static StringCycleBuilder builder() {
    return new StringCycleBuilder();
  }
}
