package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ConcreteMonoid implements Monoid {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final int size;
  private final String[] elements;
  private final Map<String, Integer> lookup;
  private final int[][] multiplicationTable;
  @Getter
  private final int identity;
  
  @Override
  public Set<String> getElementsDisplay() {
    return Set.of(elements);
  }
  
  @Override
  public String prod(String a, String b) {
    return elements[prod(lookup.get(a), lookup.get(b))];
  }
  
  @Override
  public int prod(int a, int b) {
    return multiplicationTable[a][b];
  }
  
  @Override
  public Integer eval(String a) {
    return lookup.get(a);
  }
  
  @Override
  public String display(int i) {
    return elements[i];
  }
  
  @Override
  public String getIdentityDisplay() {
    return elements[identity];
  }
  
  public static MonoidBuilder builder() {
    return new MonoidBuilder();
  }
}
