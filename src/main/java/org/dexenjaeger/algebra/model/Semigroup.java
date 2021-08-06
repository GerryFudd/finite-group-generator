package org.dexenjaeger.algebra.model;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Semigroup implements AlgebraicStructure {
  @Getter
  private final String operatorSymbol;
  private final BinaryOperator binaryOperator;
  
  private Semigroup(
    String operatorSymbol,
    BinaryOperator binaryOperator
  ) {
    this.operatorSymbol = operatorSymbol;
    this.binaryOperator = binaryOperator;
  }
  
  public static Semigroup createSemigroup(String operatorSymbol, BinaryOperator binaryOperator) {
    if (!binaryOperator.isValid()) {
      throw new RuntimeException("Semigroups may only be created from valid binary operators.");
    }
    
    if (!binaryOperator.isAssociative()) {
      throw new RuntimeException("Semigroups may only be crated from associative binary operators.");
    }
    return new Semigroup(operatorSymbol, binaryOperator);
  }
  
  public List<String> getElementsAsList() {
    return List.of(binaryOperator.getElements());
  }
  
  public String getProduct(String a, String b) {
    return binaryOperator.prod(a, b);
  }
  
  private String padOperator(String operatorSymbol) {
    return padOperator(operatorSymbol, ' ');
  }
  
  private String padOperator(String operatorSymbol, char padSymbol) {
    return String.format("%s   ", operatorSymbol).replace(' ', padSymbol).substring(0, 4);
  }
  
  private void appendLine(StringBuilder sb, String a) {
    sb.append(" ")
      .append(padOperator(a))
      .append(" |");
    
    for (String b:binaryOperator.getElements()) {
      sb.append(" ")
        .append(padOperator(binaryOperator.prod(a, b)));
    }
    sb.append(" \n");
  }
  
  public String getMultiplicationTable() {
    StringBuilder sb = new StringBuilder("\n_");
    sb.append(operatorSymbol);
    sb.append("____|_");
    sb.append(Stream.of(binaryOperator.getElements())
                .map(n -> padOperator(n, '_'))
                .collect(Collectors.joining("_")));
    sb.append("_\n");
    for (String a: binaryOperator.getElements()) {
      appendLine(sb, a);
    }
    return sb.toString();
  }
  
  public List<String> getCyclicGroup(String element) {
    List<String> cycle = new LinkedList<>();
    cycle.add(element);
    String current = binaryOperator.prod(element, element);
    while (!current.equals(element)) {
      cycle.add(current);
      current = binaryOperator.prod(current, element);
    }
    return cycle;
  }
}
