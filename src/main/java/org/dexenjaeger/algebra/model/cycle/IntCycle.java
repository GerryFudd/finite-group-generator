package org.dexenjaeger.algebra.model.cycle;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class IntCycle extends AbstractCycle<Integer, IntCycle> {
  public IntCycle(int size, Integer[] elements, int[] generators, Map<Integer, Integer> subCycleGenerators) {
    super(
      size, elements, generators, subCycleGenerators,
      spec -> builder().elements(spec).build()
    );
  }
  
  public static IntCycleBuilder builder() {
    return new IntCycleBuilder();
  }
}
