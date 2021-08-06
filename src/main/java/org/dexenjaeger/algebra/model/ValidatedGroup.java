package org.dexenjaeger.algebra.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ValidatedGroup implements Group {
  private final Map<String, String> inversesMap;
  private final Monoid withIdentity;
  
  private ValidatedGroup(
    Map<String, String> inversesMap,
    Monoid withIdentity
    ) {
    this.inversesMap = inversesMap;
    this.withIdentity = withIdentity;
  }
  
  public static ValidatedGroup createGroup(
    Map<String, String> inversesMap,
    ValidatedBinaryOperator binaryOperator,
    Function<ValidatedBinaryOperator, Monoid> withIdentityConstructor
  ) {
    if (!binaryOperator.isInverseMap(inversesMap)) {
      throw new RuntimeException("Monoids may only be created with valid inverses for all elements.");
    }
    
    return new ValidatedGroup(
      inversesMap,
      withIdentityConstructor.apply(binaryOperator)
    );
  }
  
  @Override
  public List<String> getElementsAsList() {
    return withIdentity.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return withIdentity.getProduct(a, b);
  }
  
  @Override
  public String getMultiplicationTable() {
    return withIdentity.getMultiplicationTable();
  }
  
  @Override
  public String getIdentity() {
    return withIdentity.getIdentity();
  }
  
  @Override
  public String getInverse(String element) {
    return Optional.ofNullable(inversesMap.get(element))
             .orElseThrow(() -> new RuntimeException(""));
  }
}
