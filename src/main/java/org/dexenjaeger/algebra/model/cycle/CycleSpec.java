package org.dexenjaeger.algebra.model.cycle;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CycleSpec<T> {
  private int[] generatorArray;
  private Map<Integer, Integer> subCycleGenerators;
  private Map<T, Integer> lookup;
}
