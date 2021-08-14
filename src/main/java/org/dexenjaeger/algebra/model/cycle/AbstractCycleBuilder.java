package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.utils.Builder;
import org.dexenjaeger.algebra.utils.MoreMath;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractCycleBuilder<T, U> implements Builder<U> {
  
  abstract T[] makeEmptyArray();
  
  protected T[] elements;
  
  public AbstractCycleBuilder<T, U> elements(List<T> elements) {
    this.elements = elements.toArray(makeEmptyArray());
    return this;
  }
  
  protected CycleSpec<T> resolveGenerators() {
    if (elements.length == 1) {
      return new CycleSpec<T>()
        .setGeneratorArray(new int[]{0})
        .setSubCycleGenerators(Map.of())
        .setLookup(Map.of(elements[0], 0));
    }
    LinkedList<Integer> generators = new LinkedList<>();
    Map<Integer, Integer> subCycleGenerators = new HashMap<>();
    Map<T, Integer> lookup = new HashMap<>();
    subCycleGenerators.put(1, 0);
    lookup.put(elements[0], 1);
    lookup.put(elements[elements.length - 1], 0);
    if (elements.length % 2 == 1) {
      lookup.put(elements[(elements.length + 1) / 2], (elements.length + 1) / 2 + 1);
    }
    for (int i = elements.length / 2; i > 0; i--) {
      lookup.put(elements[i], i + 1);
      lookup.put(elements[elements.length - i - 1], elements.length - i);
      if (1 < i && elements.length % i == 0) {
        subCycleGenerators.put(elements.length / i, i);
      }
      if (MoreMath.gcd(i, elements.length) == 1) {
        generators.addFirst(i);
        if (i * 2 < elements.length) {
          generators.addLast(elements.length - i);
        }
      }
    }
    
    int[] generatorArray = new int[generators.size()];
    while (!generators.isEmpty()) {
      generatorArray[generators.size()-1] = generators.removeLast();
    }
    
    return new CycleSpec<T>()
      .setGeneratorArray(generatorArray)
      .setSubCycleGenerators(subCycleGenerators)
      .setLookup(lookup);
  }
}
