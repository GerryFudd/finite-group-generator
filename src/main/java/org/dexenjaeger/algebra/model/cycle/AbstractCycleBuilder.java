package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.OrderedPair;
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
  
  protected OrderedPair<int[], Map<Integer, Integer>> resolveGenerators() {
    if (elements.length == 1) {
      return new OrderedPair<>(
        new int[]{1},
        Map.of()
      );
    }
    LinkedList<Integer> generators = new LinkedList<>();
    Map<Integer, Integer> subCycleGenerators = new HashMap<>();
    subCycleGenerators.put(1, elements.length);
    for (int i = elements.length / 2; i > 0; i--) {
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
    
    return new OrderedPair<>(
      generatorArray, subCycleGenerators
    );
  }
}
