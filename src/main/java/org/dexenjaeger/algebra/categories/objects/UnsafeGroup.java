package org.dexenjaeger.algebra.categories.objects;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UnsafeGroup implements Group {
  private final Map<String, String> inversesMap;
  private final Monoid monoid;
  
  @Override
  public String getInverse(String element) {
    return inversesMap.get(element);
  }
  
  @Override
  public String getIdentity() {
    return monoid.getIdentity();
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
}
