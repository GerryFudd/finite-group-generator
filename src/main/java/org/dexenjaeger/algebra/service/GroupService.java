package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Map<String, String> inverses = new HashMap<>();
    for (int i = 1; i < n; i++) {
      inverses.put(elements[i], elements[n-i]);
      cycle.addLast(elements[i]);
    }
    inverses.put(elements[0], elements[0]);
    cycle.addLast(elements[0]);
    return ConcreteGroup.builder()
             .operatorSymbol(operatorSymbol)
             .identityDisplay(elements[0])
             .displayInversesMap(inverses)
             .cyclesMap(Map.of(
               1, Set.of(List.of(elements[0])),
               n, Set.of(cycle)
             ))
             .elementsDisplay(Set.of(elements))
             .displayOperator(binaryOperatorSerice.createOperator(
               elements, (a, b) -> (a + b) % n
             ))
             .build();
  }
  
  public Group getGroupFromElementsAndIntTable(
    String[] elements,
    int[][] table
  ) {
    if (elements.length != table.length) {
      throw new RuntimeException("No.");
    }
    Map<String, String> inversesMap = new HashMap<>();
    inversesMap.put(elements[0], elements[0]);
    
    Set<List<String>> cycles = new HashSet<>();
    for (int i = 1; i < elements.length; i++) {
      String element = elements[i];
      if (cycles.stream().noneMatch(otherCycle -> otherCycle.contains(element))) {
        LinkedList<String> cycle = new LinkedList<>();
        cycle.addLast(elements[i]);
        int newEl = table[i][i];
        while (!cycle.contains(elements[newEl])) {
          cycle.addLast(elements[newEl]);
          newEl = table[i][newEl];
        }
        cycles.removeIf(cycle::containsAll);
        cycles.add(List.copyOf(cycle));
        cycle.removeLast();
        while (!cycle.isEmpty()) {
          String x = cycle.removeFirst();
          if (!cycle.isEmpty()) {
            String inverseX = cycle.removeLast();
            inversesMap.put(x, inverseX);
            inversesMap.put(inverseX, x);
          } else {
            inversesMap.put(x, x);
          }
        }
      }
    }
    Map<Integer, Set<List<String>>> cyclesMap = new HashMap<>();
    for (List<String> cycle:cycles) {
      cyclesMap.compute(cycle.size(), (n, nCycles) -> {
        if (nCycles == null) {
          nCycles = new HashSet<>();
        }
        nCycles.add(cycle);
        return nCycles;
      });
    }
    return ConcreteGroup.builder()
             .identityDisplay(elements[0])
             .displayInversesMap(inversesMap)
             .cyclesMap(cyclesMap)
             .elementsDisplay(Set.of(elements))
             .displayOperator(binaryOperatorSerice.createOperator(
               elements, (a, b) -> table[a][b]
             ))
             .build();
  }
}
