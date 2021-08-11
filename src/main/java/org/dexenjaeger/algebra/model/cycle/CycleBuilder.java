package org.dexenjaeger.algebra.model.cycle;

import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.utils.Builder;
import org.dexenjaeger.algebra.utils.MoreMath;

import java.util.LinkedList;
import java.util.List;

public class CycleBuilder implements Builder<Cycle> {
  private String[] elements;
  
  public CycleBuilder elements(List<String> elements) {
    this.elements = elements.toArray(new String[0]);
    return this;
  }
  
  public CycleBuilder elements(String[] elements) {
    this.elements = elements;
    return this;
  }
  
  private OrderedPair<int[], int[]> resolveGenerators() {
    if (elements.length == 1) {
      return new OrderedPair<>(
        new int[]{1},
        new int[]{}
      );
    }
    LinkedList<Integer> generators = new LinkedList<>();
    LinkedList<Integer> subCycleGenerators = new LinkedList<>();
    subCycleGenerators.addLast(elements.length);
    for (int i = elements.length / 2; i > 0; i--) {
      if (1 < i && elements.length % i == 0) {
        subCycleGenerators.addFirst(i);
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
    
    int[] subCycleGeneratorArray = new int[subCycleGenerators.size()];
    while (!subCycleGenerators.isEmpty()) {
      subCycleGeneratorArray[subCycleGenerators.size() - 1] = subCycleGenerators.removeLast();
    }
    return new OrderedPair<>(
      generatorArray, subCycleGeneratorArray
    );
  }
  
  @Override
  public Cycle build() {
    OrderedPair<int[], int[]> generatorArrays = resolveGenerators();
    return new Cycle(
      elements,
      generatorArrays.getLeft(),
      generatorArrays.getRight()
    );
  }
}
