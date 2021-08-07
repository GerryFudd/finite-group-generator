package org.dexenjaeger.algebra.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ValidatedGroup implements Group {
  private final Map<String, String> inversesMap;
  private final Monoid monoid;
  
  private ValidatedGroup(
    Map<String, String> inversesMap,
    Monoid monoid
    ) {
    this.inversesMap = inversesMap;
    this.monoid = monoid;
  }
  
  public static ValidatedGroup createGroup(ValidatedGroupSpec spec) {
    return createGroup(
      spec,
      (identity, binOp) -> ValidatedMonoid.createMonoid(new ValidatedMonoidSpec(identity, binOp))
    );
  }
  
  public static ValidatedGroup createGroup(
    ValidatedGroupSpec spec,
    BiFunction<String, ValidatingBinaryOperator, ValidatedMonoid> validatedMonoidConstructor
  ) {
    if (!spec.getBinaryOperator().isInverseMap(spec.getIdentity(), spec.getInversesMap())) {
      throw new RuntimeException("Monoids may only be created with valid inverses for all elements.");
    }
    
    return new ValidatedGroup(
      spec.getInversesMap(),
      validatedMonoidConstructor.apply(spec.getIdentity(), spec.getBinaryOperator())
    );
  }
  
  @Override
  public String getOperatorSymbol() {
    return monoid.getOperatorSymbol();
  }
  
  @Override
  public List<String> getElementsAsList() {
    return monoid.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return monoid.getProduct(a, b);
  }
  
  @Override
  public String getIdentity() {
    return monoid.getIdentity();
  }
  
  @Override
  public String getInverse(String element) {
    return Optional.ofNullable(inversesMap.get(element))
             .orElseThrow(() -> new RuntimeException(""));
  }
}
