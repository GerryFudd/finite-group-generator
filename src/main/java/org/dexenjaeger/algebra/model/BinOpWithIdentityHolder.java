package org.dexenjaeger.algebra.model;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;

public class BinOpWithIdentityHolder implements Monoid {
  @Getter
  private final String identity;
  private final Semigroup algebraicStructure;
  
  private BinOpWithIdentityHolder(String identity, Semigroup algebraicStructure) {
    this.identity = identity;
    this.algebraicStructure = algebraicStructure;
  }
  
  public static BinOpWithIdentityHolder createMonoid(
    String identity, BinaryOperator binaryOperator,
    Function<BinaryOperator, Semigroup> algebraicStructureConstructor
  ) {
    
    if (!binaryOperator.isIdentity(identity)) {
      throw new RuntimeException("Monoids may only be created with valid identity elements.");
    }
    
    return new BinOpWithIdentityHolder(
      identity,
      algebraicStructureConstructor.apply(binaryOperator)
    );
  }
  
  @Override
  public List<String> getElementsAsList() {
    return algebraicStructure.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return algebraicStructure.getProduct(a, b);
  }
  
  @Override
  public String getMultiplicationTable() {
    return algebraicStructure.getMultiplicationTable();
  }
  
}
