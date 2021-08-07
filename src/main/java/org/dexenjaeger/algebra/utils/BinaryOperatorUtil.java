package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Group;
import org.dexenjaeger.algebra.model.ValidatingBinaryOperator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BinaryOperatorUtil {
  
  public static ValidatingBinaryOperator getSortedAndPrettifiedBinaryOperator(
    int size,
    BiFunction<Integer, Integer, Integer> binOp
  ) {
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
    if (identity.isPresent()) {
      remapper.map("I", identity.get()).orElseThrow(() ->
        new RuntimeException("The identity disappeared before it was set.")
      );
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
    for (int cycleSize: summary.getCycleSizes()) {
      summary.getNCycles(cycleSize).forEach(cycle -> {
        Optional<String> baseValue = remapper.map(cycle.get(0));
        if (baseValue.isEmpty()) {
          return;
        }
        int i = 1;
        while (i < cycle.size()) {
          remapper.map(baseValue.get() + (i + 1), cycle.get(i));
          i++;
        }
      });
    }
    remapper.getAvailable().forEach(remapper::map);
    return remapper.createBinaryOperator(binOp);
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
  
  public static String getMultiplicationTable(
    String operatorSymbol,
    List<String> elementsList,
    BiFunction<String, String, String> binaryOperator
  ) {
    StringBuilder sb = new StringBuilder("\n_");
    sb.append(operatorSymbol);
    sb.append("____|_");
    sb.append(elementsList.stream()
                .map(n -> padOperator(n, '_'))
                .collect(Collectors.joining("_")));
    sb.append("_\n");
    for (String a: elementsList) {
      appendLine(
        sb, a, elementsList.stream()
                 .map(b -> binaryOperator.apply(a, b))
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
  
  public static boolean isSubgroup(Group domain, Group kernel) {
    for (String a:kernel.getElementsAsList()) {
      for (String b:kernel.getElementsAsList()) {
        String c = domain.getProduct(a, b);
        if (!kernel.getElementsAsList().contains(c)) {
          return false;
        }
        if (!c.equals(kernel.getProduct(a, b))) {
          return false;
        }
      }
    }
    return true;
  }
}
