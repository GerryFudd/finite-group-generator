package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupService {
  public Group getCyclicGroup(String... elements) {
    return getCyclicGroup(elements, "*");
  }
  
  public Group getCyclicGroup(String[] elements, String operatorSymbol) {
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
    
    Set<List<Integer>> cycles = new HashSet<>();
    for (int i = 1; i < elements.length; i++) {
      int curr = i;
      if (cycles.stream().noneMatch(otherCycle -> otherCycle.contains(curr))) {
        LinkedList<Integer> intCycle = new LinkedList<>();
        intCycle.addLast(i);
        int newEl = table[i][i];
        while (!intCycle.contains(newEl)) {
          intCycle.addLast(newEl);
          newEl = table[i][newEl];
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
    return Group.builder()
             .inversesMap(inversesMap)
             .maximalCycles(cycles.stream()
                              .map(cycle -> IntCycle.builder()
                                              .elements(cycle)
                                              .build())
                              .collect(Collectors.toSet()))
             .identity(0)
             .elements(elements)
             .multiplicationTable(table)
             .build();
  }
}
