package org.dexenjaeger.algebra.categories.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TrivialGroup implements SafeGroup {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final String identity;
  
  public TrivialGroup() {
    this("I");
  }
  public TrivialGroup(String identity) {
    this("*", identity);
  }
  
  @Override
  public String getInverse(String element) {
    return identity;
  }
  
  @Override
  public List<String> getElementsAsList() {
    return List.of(identity);
  }
  
  @Override
  public String getProduct(String a, String b) {
    return identity;
  }
}
