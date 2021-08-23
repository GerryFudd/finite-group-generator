package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.cycle.Cycle;
import org.dexenjaeger.algebra.model.cycle.ElementCycle;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.cycle.MappingCycle;
import org.dexenjaeger.algebra.model.spec.CycleSpec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CycleUtils {
  private <T> CycleSpec<T> resolveGenerators(List<T> elements) {
    int n = elements.size();
    if (n == 0) {
      throw new RuntimeException("Empty cycles are not allowed.");
    }
    if (n == 1) {
      return new CycleSpec<T>()
               .setGeneratorArray(new int[]{0})
               .setSubCycleGenerators(Map.of())
               .setLookup(Map.of(elements.get(0), 0));
    }
    LinkedList<Integer> generators = new LinkedList<>();
    Map<Integer, Integer> subCycleGenerators = new HashMap<>();
    Map<T, Integer> lookup = new HashMap<>();
    subCycleGenerators.put(1, 0);
    lookup.put(elements.get(0), 1);
    lookup.put(elements.get(n - 1), 0);
    if (n % 2 == 1) {
      lookup.put(elements.get((n + 1) / 2), (n + 1) / 2 + 1);
    }
    for (int i = n / 2; i > 0; i--) {
      lookup.put(elements.get(i), i + 1);
      lookup.put(elements.get(n - i - 1), n - i);
      if (1 < i && n % i == 0) {
        subCycleGenerators.put(n / i, i);
      }
      if (MoreMath.gcd(i, n) == 1) {
        generators.addFirst(i);
        if (i * 2 < n) {
          generators.addLast(n - i);
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
  
  public Set<IntCycle> createSingleIntCycle(Integer... elements) {
    return Set.of(createIntCycle(elements));
  }
  
  public IntCycle createIntCycle(Integer... elements) {
    return createIntCycle(List.of(elements));
  }
  
  public IntCycle createIntCycle(List<Integer> elements) {
    CycleSpec<Integer> spec = resolveGenerators(elements);
    return IntCycle.builder()
             .elements(elements)
             .generators(spec.getGeneratorArray())
             .subCycleGenerators(spec.getSubCycleGenerators())
             .lookup(spec.getLookup())
             .maker(this::createIntCycle)
             .build();
  }
  
  public ElementCycle createElementCycle(Element... elements) {
    return createElementCycle(List.of(elements));
  }
  
  public ElementCycle createElementCycle(List<Element> elements) {
    CycleSpec<Element> spec = resolveGenerators(elements);
    return ElementCycle.builder()
             .elements(elements)
             .generators(spec.getGeneratorArray())
             .subCycleGenerators(spec.getSubCycleGenerators())
             .lookup(spec.getLookup())
             .maker(this::createElementCycle)
             .build();
  }
  
  public MappingCycle createMappingCycle(List<Mapping> mappings) {
    CycleSpec<Mapping> spec = resolveGenerators(mappings);
    return MappingCycle.builder()
             .elements(mappings)
             .generators(spec.getGeneratorArray())
             .subCycleGenerators(spec.getSubCycleGenerators())
             .lookup(spec.getLookup())
             .maker(this::createMappingCycle)
             .build();
  }
  
  public <T> IntCycle convertToIntCycle(Function<T, Integer> mapper, Cycle<T> cycle) {
    return convertToIntCycle(mapper, cycle.getElements());
  }
  @SafeVarargs
  public final <T> IntCycle convertToIntCycle(Function<T, Integer> mapper, T... cycleElements) {
    return convertToIntCycle(mapper, List.of(cycleElements));
  }
  public <T> IntCycle convertToIntCycle(Function<T, Integer> mapper, List<T> cycleElements) {
    return createIntCycle(cycleElements.stream()
      .map(mapper)
      .collect(Collectors.toList()));
  }
}
