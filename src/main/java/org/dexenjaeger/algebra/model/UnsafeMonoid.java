package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UnsafeMonoid implements Monoid {
  @Getter
  private final String identity;
  private final Semigroup semigroup;
  
  @Override
  public String getOperatorSymbol() {
    return semigroup.getOperatorSymbol();
  }
  
  @Override
  public List<String> getElementsAsList() {
    return semigroup.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return semigroup.getProduct(a, b);
  }
}
