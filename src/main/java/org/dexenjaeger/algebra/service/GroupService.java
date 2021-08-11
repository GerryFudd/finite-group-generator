package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupService {
  private final BinaryOperatorService binaryOperatorSerice;
  
  @Inject
  public GroupService(BinaryOperatorService binaryOperatorSerice) {
    this.binaryOperatorSerice = binaryOperatorSerice;
  }
  
  public Group getCyclicGroup(String... elements) {
    return getCyclicGroup(elements, "*");
  }
  
  public Group getCyclicGroup(String[] elements, String operatorSymbol) {
    int n = elements.length;
    LinkedList<String> cycle = new LinkedList<>();
    Map<Integer, Integer> inverses = new HashMap<>();
    for (int i = 1; i < n; i++) {
      inverses.put(i, n - i);
      cycle.addLast(elements[i]);
    }
    inverses.put(0, 0);
    cycle.addLast(elements[0]);
    return Group.builder()
             .inversesMap(inverses)
             .maximalCycles(Set.of(StringCycle.builder().elements(cycle).build()))
             .identity(0)
             .operatorSymbol(operatorSymbol)
             .elements(elements)
             .operator((a, b) -> (a + b) % n)
             .build();
  }
  
  public Group getGroupFromElementsAndIntTable(
    String[] elements,
    int[][] table
  ) {
    if (elements.length != table.length) {
      throw new RuntimeException("No.");
    }
    Map<Integer, Integer> inversesMap = new HashMap<>();
    inversesMap.put(0, 0);
    
    Set<List<String>> cycles = new HashSet<>();
    for (int i = 1; i < elements.length; i++) {
      String element = elements[i];
      if (cycles.stream().noneMatch(otherCycle -> otherCycle.contains(element))) {
        LinkedList<String> cycle = new LinkedList<>();
        LinkedList<Integer> intCycle = new LinkedList<>();
        cycle.addLast(elements[i]);
        intCycle.addLast(i);
        int newEl = table[i][i];
        while (!intCycle.contains(newEl)) {
          cycle.addLast(elements[newEl]);
          intCycle.addLast(newEl);
          newEl = table[i][newEl];
        }
        cycles.removeIf(cycle::containsAll);
        cycles.add(List.copyOf(cycle));
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
    return Group.builder()
             .inversesMap(inversesMap)
             .maximalCycles(cycles.stream()
                              .map(cycle -> StringCycle.builder()
                                              .elements(cycle)
                                              .build())
                              .collect(Collectors.toSet()))
             .identity(0)
             .elements(elements)
             .multiplicationTable(table)
             .build();
  }
}
