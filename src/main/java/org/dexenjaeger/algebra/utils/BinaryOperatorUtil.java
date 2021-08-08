package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperator;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BinaryOperatorUtil {
  
  public static BinaryOperatorSummary getSortedAndPrettifiedBinaryOperator(
    int size,
    BiFunction<Integer, Integer, Integer> binOp
  ) {
    BinaryOperatorSummary.BinaryOperatorSummaryBuilder resultBuilder = BinaryOperatorSummary.builder();
    Remapper remapper = Remapper.init(size);
    
    RawBinaryOperatorSummary summary = new RawBinaryOperatorSummary();
    for (int i = 0; i < size; i++) {
      boolean isLeftIdentity = true;
      boolean isRightIdentity = true;
      for (int j = 0; j < size; j++) {
        isLeftIdentity = isLeftIdentity && binOp.apply(i, j) == j;
        isRightIdentity = isRightIdentity && binOp.apply(j, i) == j;
      }
      if (isLeftIdentity) {
        summary.getLeftIdentities().add(i);
      }
      if (isRightIdentity) {
        summary.getRightIdentities().add(i);
      }
      
      List<Integer> cycle = new LinkedList<>();
      cycle.add(i);
      int next = binOp.apply(i, i);
      while (!cycle.contains(next)) {
        cycle.add(next);
        next = binOp.apply(i, next);
      }
      summary.addCycle(cycle);
    }
    Optional<Integer> identity = summary.getIdentity();
    Map<String, String> inverseMap = new HashMap<>();
    if (identity.isPresent()) {
      inverseMap.put("I", "I");
      resultBuilder.identity("I").inverseMap(inverseMap);
      remapper.map("I", identity.get()).orElseThrow();
    } else {
      int l = 1;
      int r = 1;
      for (int leftIdentity:summary.getLeftIdentities()) {
        if (remapper.map("L" + l, leftIdentity).isPresent()) {
          l++;
        }
      }
      for (int rightIdentity:summary.getRightIdentities()) {
        if (remapper.map("R" + r, rightIdentity).isPresent()) {
          r++;
        }
      }
    }
    Map<Integer, Set<List<String>>> cyclesMap = new HashMap<>();
    for (int cycleSize: summary.getCycleSizes()) {
      summary.getNCycles(cycleSize).forEach(cycle -> {
        cyclesMap.computeIfAbsent(cycleSize, (acc) -> new HashSet<>());
        List<String> resultCycle = new LinkedList<>();
        String baseValue = remapper.map(cycle.get(0)).orElseThrow();
        resultCycle.add(baseValue);
        int i = 1;
        while (i < cycle.size() - 1) {
          resultCycle.add(remapper.map(baseValue + (i + 1), cycle.get(i)).orElseThrow());
          i++;
        }
  
        LinkedList<String> cycleVals = new LinkedList<>(resultCycle);
        resultCycle.add("I");
        cyclesMap.get(cycleSize).add(resultCycle);
        while (!cycleVals.isEmpty()) {
          String val = cycleVals.removeFirst();
          String inv = cycleVals.isEmpty() ? val : cycleVals.removeLast();
          inverseMap.put(val, inv);
          inverseMap.put(inv, val);
        }
      });
    }
    resultBuilder.cyclesMap(cyclesMap);
    if (!remapper.getAvailable().isEmpty()) {
      throw new RuntimeException("All permutations should exist in some cycle.");
    }
    return resultBuilder
             .binaryOperator(remapper.createBinaryOperator(binOp))
             .build();
  }
  
  private static String padOperator(String operatorSymbol) {
    return padOperator(operatorSymbol, ' ');
  }
  
  private static String padOperator(String operatorSymbol, char padSymbol) {
    return String.format("%s   ", operatorSymbol).replace(' ', padSymbol).substring(0, 4);
  }
  
  private static void appendLine(
    StringBuilder sb, String a,
    List<String> products
  ) {
    sb.append(" ")
      .append(padOperator(a))
      .append(" |");
    
    for (String b:products) {
      sb.append(" ")
        .append(padOperator(b));
    }
    sb.append(" \n");
  }
  
  private static Comparator<String> getElementComparator(String identity) {
    if (identity == null) {
      return Comparator.naturalOrder();
    }
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
  
  public static List<String> getSortedElements(Collection<String> elements, String identity) {
    return elements.stream()
             .sorted(getElementComparator(identity)).collect(Collectors.toList());
  }
  
  public static String getMultiplicationTable(
    BinaryOperator binaryOperator
  ) {
    return getMultiplicationTable(binaryOperator, null);
  }
  public static String getMultiplicationTable(
    BinaryOperator binaryOperator,
    String identity
  ) {
    List<String> elementsList = getSortedElements(binaryOperator.getElements(), identity);
    
    StringBuilder sb = new StringBuilder("\n_");
    sb.append(binaryOperator.getOperatorSymbol());
    sb.append("____|_");
    sb.append(elementsList.stream()
                .map(n -> padOperator(n, '_'))
                .collect(Collectors.joining("_")));
    sb.append("_\n");
    for (String a: elementsList) {
      appendLine(
        sb, a, elementsList.stream()
                 .map(b -> binaryOperator.prod(a, b))
                 .collect(Collectors.toList())
      );
    }
    return sb.toString();
  }
  
  public static List<String> getCyclicGroup(String element, BiFunction<String, String, String> binaryOperator) {
    List<String> cycle = new LinkedList<>();
    cycle.add(element);
    String current = binaryOperator.apply(element, element);
    while (!current.equals(element)) {
      cycle.add(current);
      current = binaryOperator.apply(current, element);
    }
    return cycle;
  }
  
  public static void validateSubgroup(Group domain, Group kernel) {
    RuntimeException e = new RuntimeException("Subset is not a subgroup.");
    for (String a:kernel.getElements()) {
      for (String b:kernel.getElements()) {
        String c = domain.prod(a, b);
        if (!kernel.getElements().contains(c)) {
          throw e;
        }
        if (!c.equals(kernel.prod(a, b))) {
          throw e;
        }
      }
    }
  }
}
