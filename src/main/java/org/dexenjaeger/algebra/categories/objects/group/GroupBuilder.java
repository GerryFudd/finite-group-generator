package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.model.Cycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GroupBuilder {
  GroupBuilder() {}
  
  private String operatorSymbol = "*";
  private String identityDisplay = "I";
  
  private Integer size;
  private String[] elements;
  private int[][] multiplicationTable;
  
  private Integer identity;
  private Map<String, Integer> lookup;
  private Map<Integer, Integer> inversesMap;
  private BiFunction<Integer, Integer, Integer> operator;
  
  private Map<Integer, Set<List<String>>> cyclesMap;
  private Set<Cycle> maximalCycles;
  
  private Set<String> elementsDisplay;
  private Map<String, String> displayInversesMap;
  private BiFunction<String, String, String> displayOperator;
  
  public GroupBuilder operatorSymbol(String operatorSymbol) {
    this.operatorSymbol = operatorSymbol;
    return this;
  }
  
  public GroupBuilder size(int size) {
    this.size = size;
    return this;
  }
  
  public GroupBuilder elements(String[] elements) {
    this.elements = elements;
    return this;
  }
  
  public GroupBuilder identity(int identity) {
    this.identity = identity;
    return this;
  }
  
  public GroupBuilder inversesMap(Map<Integer, Integer> inversesMap) {
    this.inversesMap = inversesMap;
    return this;
  }
  
  public GroupBuilder operator(BiFunction<Integer, Integer, Integer> operator) {
    this.operator = operator;
    return this;
  }
  
  public GroupBuilder multiplicationTable(int[][] multiplicationTable) {
    this.multiplicationTable = multiplicationTable;
    return this;
  }
  
  public GroupBuilder identityDisplay(String identityDisplay) {
    this.identityDisplay = identityDisplay;
    return this;
  }
  
  public GroupBuilder elementsDisplay(Set<String> elementsDisplay) {
    this.elementsDisplay = elementsDisplay;
    return this;
  }
  
  public GroupBuilder displayInversesMap(Map<String, String> inversesMap) {
    this.displayInversesMap = inversesMap;
    return this;
  }
  
  public GroupBuilder cyclesMap(Map<Integer, Set<List<String>>> cyclesMap) {
    this.cyclesMap = cyclesMap;
    return this;
  }
  
  public GroupBuilder displayOperator(BiFunction<String, String, String> displayOperator) {
    this.displayOperator = displayOperator;
    return this;
  }
  
  public Group build() {
    if (elements == null) {
      elements = new String[elementsDisplay.size()];
      if (lookup == null) {
        lookup = new HashMap<>();
        identity = 0;
        List<String> sortedElements = BinaryOperatorUtil.getSortedElements(
          elementsDisplay, identityDisplay
        );
        for (int i = 0; i < elements.length; i++) {
          elements[i] = sortedElements.get(i);
          lookup.put(sortedElements.get(i), i);
        }
      } else {
        identity = lookup.get(identityDisplay);
        for (Map.Entry<String, Integer> entry:lookup.entrySet()) {
          elements[entry.getValue()] = entry.getKey();
        }
      }
    }
    if (lookup == null) {
      lookup = new HashMap<>();
      for (int i = 0; i < elements.length; i++) {
        lookup.put(elements[i], i);
      }
    }
    if (identity == null) {
      identity = lookup.get(identityDisplay);
    }
    if (inversesMap == null) {
      inversesMap = displayInversesMap.entrySet().stream()
        .collect(Collectors.toMap(
          entry -> lookup.get(entry.getKey()),
          entry -> lookup.get(entry.getValue())
        ));
    }
    
    if (maximalCycles == null) {
      if (elements.length == 1) {
        maximalCycles = Set.of(Cycle.builder()
                        .elements(List.of(identityDisplay))
                        .build());
      } else {
        maximalCycles = cyclesMap.entrySet().stream()
          .filter(entry -> entry.getKey() > 1)
          .map(Map.Entry::getValue)
          .flatMap(Set::stream)
          .map(cycleElements -> Cycle.builder()
                                  .elements(cycleElements)
                                  .build())
          .collect(Collectors.toSet());
      }
    }
    
    if (operator == null) {
      operator = (a, b) -> lookup.get(displayOperator.apply(elements[a], elements[b]));
    }
    
    if (multiplicationTable == null) {
      multiplicationTable = new int[elements.length][elements.length];
      for (int i = 0; i < elements.length; i++) {
        for (int j = 0; j < elements.length; j++) {
          multiplicationTable[i][j] = operator.apply(i, j);
        }
      }
    }
    
    return new ConcreteGroup(
      operatorSymbol,
      identity,
      elements,
      multiplicationTable,
      lookup,
      inversesMap,
      maximalCycles,
      cyclesMap
    );
  }
}
