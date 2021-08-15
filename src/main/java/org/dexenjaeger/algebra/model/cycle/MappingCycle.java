package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.Mapping;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MappingCycle extends AbstractCycle<Mapping, MappingCycle> {
  MappingCycle(
    int size, Mapping[] elements,
    Map<Mapping, Integer> lookup, int[] generators,
    Map<Integer, Integer> subCycleGenerators,
    Function<List<Mapping>, MappingCycle> make) {
    super(size, elements, lookup, generators, subCycleGenerators, make);
  }
  
  public static MappingCycleBuilder builder() {
    return new MappingCycleBuilder();
  }
}
