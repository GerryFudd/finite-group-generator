package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

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
  public List<Integer> getCycleSizes() {
    return List.of(1);
  }
  
  @Override
  public Set<List<String>> getNCycles(Integer n) {
    return n == 1 ? Set.of(List.of("I")) : Set.of();
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
