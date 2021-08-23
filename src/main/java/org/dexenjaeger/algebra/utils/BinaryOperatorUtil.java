package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BinaryOperatorUtil {
  private final CycleUtils cycleUtils = new CycleUtils();
  private static String padOperator(String operatorSymbol, int width) {
    return padOperator(operatorSymbol, ' ', width);
  }
  
  private static String padOperator(String operatorSymbol, char padSymbol, int width) {
    char[] charArray = new char[width];
    Arrays.fill(charArray, padSymbol);
    return new StringBuilder(operatorSymbol)
      .append(String.valueOf(charArray))
      .substring(0, width);
  }
  
  private static void appendLine(
    StringBuilder sb, String a, int width,
    List<String> products
  ) {
    sb.append(" ")
      .append(padOperator(a, width))
      .append(" |");
    
    for (String b:products) {
      sb.append(" ")
        .append(padOperator(b, width));
    }
    sb.append(" \n");
  }
  
  public static Comparator<String> getElementComparator(String identity) {
    return  (a, b) -> {
      if (a.equals(identity) && !b.equals(identity)) {
        return -1;
      }
      if (b.equals(identity) && !a.equals(identity)) {
        return 1;
      }
      return a.compareTo(b);
    };
  }
  
  public static String printMultiplicationTable(
    BinaryOperator binaryOperator
  ) {
    return printMultiplicationTable(
      binaryOperator.getOperatorSymbol().getAscii(),
      binaryOperator.getSortedElements(),
      getSafeOperator(binaryOperator)
    );
  }
  
  private static BiFunction<Element, Element, String> getSafeOperator(BinaryOperator binaryOperator) {
    return (a, b) -> {
      try {
        return binaryOperator.prod(a, b).toString();
      } catch (RuntimeException e) {
        System.out.printf("Couldn't find product of %s and %s while printing table.", a, b);
      }
  
      Integer i = binaryOperator.eval(a);
      Integer j = binaryOperator.eval(b);
      if (i == null || j == null) {
        return "?";
      }
      try {
        int k = binaryOperator.prod(i, j);
        if (k < 0 || k >= binaryOperator.getSize()) {
          return String.format("[%d?]", k);
        }
      } catch (RuntimeException e) {
        System.out.printf("Couldn't find product of %d and %d while printing table.", i, j);
      }
      return "?";
    };
  }
  
  private static String printMultiplicationTable(
    String operatorSymbol,
    List<Element> elementsList,
    BiFunction<Element, Element, String> safeOperator
  ) {
    Map<Element, List<String>> rowMap = elementsList.stream()
      .collect(Collectors.toMap(
        Function.identity(),
        a -> elementsList.stream()
               .map(b -> safeOperator.apply(a, b))
               .collect(Collectors.toList())
      ));
    int width = rowMap.values().stream()
                  .flatMap(List::stream)
                  .map(String::length)
                  .max(Comparator.naturalOrder())
                  .orElseThrow();
    
    StringBuilder sb = new StringBuilder("\n_");
    sb.append(padOperator(operatorSymbol, '_', width));
    sb.append("_|_");
    sb.append(elementsList.stream()
                .map(n -> padOperator(n.getAscii(), '_', width))
                .collect(Collectors.joining("_")));
    sb.append("_\n");
    for (Element a: elementsList) {
      appendLine(
        sb, a.toString(), width, rowMap.get(a)
      );
    }
    return sb.toString();
  }
  
  public List<String> getCycle(String element, BiFunction<String, String, String> binaryOperator) {
    List<String> cycle = new LinkedList<>();
    cycle.add(element);
    String current = binaryOperator.apply(element, element);
    while (!current.equals(element)) {
      cycle.add(current);
      current = binaryOperator.apply(current, element);
    }
    return cycle;
  }
  
  public BiFunction<String, String, String> createOperator(
    String[] elements, BiFunction<Integer, Integer, Integer> intOp
  ) {
    Map<String, Integer> lookup = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      lookup.put(elements[i], i);
    }
    return (a, b) -> elements[intOp.apply(lookup.get(a), lookup.get(b))];
  }
  
  public int[][] getMultiplicationTable(int size, BiFunction<Integer, Integer, Integer> operator) {
    try {
      int[][] multiplicationTable = new int[size][size];
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          multiplicationTable[i][j] = operator.apply(i, j);
        }
      }
      return multiplicationTable;
    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to build multiplication table.", e);
    }
  }
  
  public Map<Element, Integer> createLookup(Element[] elements) {
    Map<Element, Integer> lookup = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      lookup.put(elements[i], i);
    }
    return lookup;
  }
  
  public Set<IntCycle> getMaximalCycles(
    int size, BiFunction<Integer, Integer, Integer> operator
  ) {
    Set<IntCycle> result = new HashSet<>();
    for (int i = 0; i < size; i++) {
      List<Integer> cycleElements = new LinkedList<>();
      cycleElements.add(i);
      int next = operator.apply(i, i);
      while (!cycleElements.contains(next)) {
        cycleElements.add(next);
        next = operator.apply(i, next);
      }
      IntCycle candidateCycle = cycleUtils.createIntCycle(cycleElements);
      if (result.stream().anyMatch(
        existingCycle -> existingCycle.isParentOf(candidateCycle)
      )) {
        continue;
      }
      result.removeIf(candidateCycle::isParentOf);
      result.add(candidateCycle);
    }
    return result;
  }
  
  public Map<Integer, Integer> getInversesMap(
    int size, int identity,
    BiFunction<Integer, Integer, Integer> operator) {
    Map<Integer, Integer> result = new HashMap<>();
    result.put(identity, identity);
    for (int i = 0; i < size; i++) {
      if (result.containsKey(i)) {
        continue;
      }
      for (int j = 0; j < size; j++) {
        if (result.containsKey(j)) {
          continue;
        }
        if (identity == operator.apply(i, j)) {
          result.put(i, j);
          result.put(j, i);
          break;
        }
      }
    }
    return result;
  }
}
