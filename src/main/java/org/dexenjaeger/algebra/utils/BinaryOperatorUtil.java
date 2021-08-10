package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BinaryOperatorUtil {
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
  
  public static String printMultiplicationTable(
    BinaryOperator binaryOperator
  ) {
    return printMultiplicationTable(binaryOperator, null);
  }
  
  public static String printMultiplicationTable(
    BinaryOperator binaryOperator,
    String identity
  ) {
    List<String> elementsList = getSortedElements(binaryOperator.getElementsDisplay(), identity);
    
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
  
  public static List<String> getCycle(String element, BiFunction<String, String, String> binaryOperator) {
    List<String> cycle = new LinkedList<>();
    cycle.add(element);
    String current = binaryOperator.apply(element, element);
    while (!current.equals(element)) {
      cycle.add(current);
      current = binaryOperator.apply(current, element);
    }
    return cycle;
  }
  
  public static BiFunction<String, String, String> createOperator(
    String[] elements, BiFunction<Integer, Integer, Integer> intOp
  ) {
    Map<String, Integer> lookup = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      lookup.put(elements[i], i);
    }
    return (a, b) -> elements[intOp.apply(lookup.get(a), lookup.get(b))];
  }
}
