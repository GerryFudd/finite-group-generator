package org.dexenjaeger.algebra.categories.objects.group;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UnsafeGroup implements Group {
  private final Map<String, String> inversesMap;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  private final Monoid monoid;
  
  @Override
  public String getInverse(String element) {
    return inversesMap.get(element);
  }
  
  @Override
  public List<Integer> getCycleSizes() {
    return cyclesMap.keySet().stream().sorted().collect(Collectors.toList());
  }
  
  @Override
  public Set<List<String>> getNCycles(Integer n) {
    return cyclesMap.containsKey(n) ? cyclesMap.get(n) : Set.of();
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
  public Set<String> getElements() {
    return monoid.getElements();
  }
  
  @Override
  public String prod(String a, String b) {
    return monoid.prod(a, b);
  }
}
