package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.SortedGroupResult;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.Remapper;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupService {
  private final Validator<Group> groupValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  
  @Inject
  public GroupService(
    Validator<Group> groupValidator,
    BinaryOperatorUtil binaryOperatorUtil
  ) {
    this.groupValidator = groupValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
  }
  
  public Group createCyclicGroup(String... elements) {
    return createCyclicGroup(elements, "*");
  }
  
  public Group createCyclicGroup(String[] elements, String operatorSymbol) {
    int n = elements.length;
    LinkedList<Integer> cycle = new LinkedList<>();
    Map<Integer, Integer> inverses = new HashMap<>();
    for (int i = 1; i < n; i++) {
      inverses.put(i, n - i);
      cycle.addLast(i);
    }
    inverses.put(0, 0);
    cycle.addLast(0);
    return Group.builder()
             .inversesMap(inverses)
             .maximalCycles(Set.of(IntCycle.builder().elements(cycle).build()))
             .identity(0)
             .operatorSymbol(operatorSymbol)
             .elements(elements)
             .size(n)
             .lookup(binaryOperatorUtil.createLookup(elements))
             .multiplicationTable(
               binaryOperatorUtil.getMultiplicationTable(
                 n, (a, b) -> (a + b) % n
               )
             )
             .build();
  }
  
  public Group createGroup(
    String operatorSymbol,
    int identity,
    String[] elements,
    Map<Integer, Integer> inversesMap,
    Set<IntCycle> maximalCycles,
    BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    return createGroup(
      operatorSymbol, identity, elements,
      binaryOperatorUtil.createLookup(elements),
      inversesMap, maximalCycles, operator
    );
  }
  
  private Group createGroup(
    String operatorSymbol,
    int identity,
    String[] elements,
    Map<String, Integer> lookup,
    Map<Integer, Integer> inversesMap,
    Set<IntCycle> maximalCycles,
    BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    Group result = Group.builder()
                    .inversesMap(inversesMap)
                    .maximalCycles(maximalCycles)
                    .identity(identity)
                    .size(elements.length)
                    .elements(elements)
                    .operatorSymbol(operatorSymbol)
                    .lookup(lookup)
                    .multiplicationTable(binaryOperatorUtil.getMultiplicationTable(
                      elements.length,
                      operator
                    ))
                    .build();
  
    groupValidator.validate(result);
    return result;
  }
  
  public SortedGroupResult createSortedGroup(
    String[] elements,
    Map<Integer, Integer> inversesMap,
    Set<IntCycle> maximalCycles,
    BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    return createSortedGroup(
      "*", elements, inversesMap, maximalCycles, operator
    );
  }
  
  public SortedGroupResult createSortedGroup(
    String operatorSymbol,
    String[] elements,
    Map<Integer, Integer> inversesMap,
    Set<IntCycle> maximalCycles,
    BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    Remapper remapper = Remapper.init(elements.length);
    Map<Integer, Set<Integer>> nCycleGenerators = new HashMap<>();
    for (IntCycle cycle:maximalCycles.stream()
                          .flatMap(cycle -> Stream.concat(
                            Stream.of(cycle),
                            cycle.getSubCycles().stream()
                          )).collect(Collectors.toSet())) {
      nCycleGenerators.computeIfPresent(cycle.getSize(), (n, generators) -> {
        generators.addAll(cycle.getGenerators());
        return generators;
      });
      nCycleGenerators.computeIfAbsent(cycle.getSize(), n -> new HashSet<>(cycle.getGenerators()));
    }
    
    nCycleGenerators.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .map(Map.Entry::getValue)
      .forEach(nGenerators -> nGenerators.stream()
                                .sorted(Comparator.comparing(i -> elements[i]))
                                .forEach(generator -> remapper.map(elements[generator], generator)));
    
    
    return new SortedGroupResult(
      createGroup(
        operatorSymbol,
        0, // The identity will be the only 1-cycle in a valid group
        remapper.getElements(),
        remapper.getReverseLookup(),
        remapper.remapInverses(inversesMap),
        remapper.remapCycles(maximalCycles),
        remapper.remapBiFunc(operator)
      ), remapper
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    String[] elements,
    int[][] multiplicationTable
  ) throws ValidationException {
    return constructGroupFromElementsAndMultiplicationTable(
      "*", elements, multiplicationTable
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    String operatorSymbol,
    String[] elements,
    int[][] multiplicationTable
  ) throws ValidationException {
    if (elements.length != multiplicationTable.length) {
      throw new RuntimeException("No.");
    }
    Map<Integer, Integer> inversesMap = new HashMap<>();
    inversesMap.put(0, 0);
    
    Set<List<Integer>> cycles = new HashSet<>();
    for (int i = 1; i < elements.length; i++) {
      int curr = i;
      if (cycles.stream().noneMatch(otherCycle -> otherCycle.contains(curr))) {
        LinkedList<Integer> intCycle = new LinkedList<>();
        intCycle.addLast(i);
        int newEl = multiplicationTable[i][i];
        while (!intCycle.contains(newEl)) {
          intCycle.addLast(newEl);
          newEl = multiplicationTable[i][newEl];
        }
        cycles.removeIf(intCycle::containsAll);
        cycles.add(List.copyOf(intCycle));
        intCycle.removeLast();
        while (!intCycle.isEmpty()) {
          int x = intCycle.removeFirst();
          if (!intCycle.isEmpty()) {
            int inverseX = intCycle.removeLast();
            inversesMap.put(x, inverseX);
            inversesMap.put(inverseX, x);
          } else {
            inversesMap.put(x, x);
          }
        }
      }
    }
    Group result = createSortedGroup(
      operatorSymbol,
      elements,
      inversesMap,
      cycles.stream()
        .map(cycle -> IntCycle.builder()
                        .elements(cycle)
                        .build())
        .collect(Collectors.toSet()),
      (i, j) -> multiplicationTable[i][j]
    ).getGroup();
    
    groupValidator.validate(result);
    return result;
  }
}
