package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.Cycle;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class TrivialGroup implements Group {
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
    return n == 1 ? Set.of(List.of(identity)) : Set.of();
  }
  
  @Override
  public Set<Cycle> getMaximalCycles() {
    return Set.of(Cycle.builder()
                    .elements(List.of(identity))
                    .build());
  }
  
  @Override
  public Set<String> getElements() {
    return Set.of(identity);
  }
  
  @Override
  public String prod(String a, String b) {
    return identity;
  }
}
