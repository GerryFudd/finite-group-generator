package org.dexenjaeger.algebra.model;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidatedSemigroup implements Semigroup {
  @Getter
  private final String operatorSymbol;
  private final ValidatingBinaryOperator binaryOperator;
  
  private ValidatedSemigroup(
    String operatorSymbol,
    ValidatingBinaryOperator binaryOperator
  ) {
    this.operatorSymbol = operatorSymbol;
    this.binaryOperator = binaryOperator;
  }
  
  public static ValidatedSemigroup createSemigroup(ValidatedSemigroupSpec spec) {
    if (!spec.getBinaryOperator().isValid()) {
      throw new RuntimeException("Semigroups may only be created from valid binary operators.");
    }
    
    if (!spec.getBinaryOperator().isAssociative()) {
      throw new RuntimeException("Semigroups may only be crated from associative binary operators.");
    }
    
    return new ValidatedSemigroup(
      spec.getOperatorSymbol(),
      spec.getBinaryOperator()
    );
  }
  
  public List<String> getElementsAsList() {
    return List.of(binaryOperator.getElements());
  }
  
  public String getProduct(String a, String b) {
    return binaryOperator.prod(a, b);
  }
}
