package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class TrivialGroup implements Group {
  @Getter
  private final String operatorSymbol;
  @Getter
  private final String identityDisplay;
  
  public TrivialGroup() {
    this("I");
  }
  public TrivialGroup(String identityDisplay) {
    this("*", identityDisplay);
  }
  
  @Override
  public String getInverse(String element) {
    return identityDisplay;
  }
  
  @Override
  public int getInverse(int element) {
    return 0;
  }
  
  @Override
  public List<Integer> getCycleSizes() {
    return List.of(1);
  }
  
  @Override
  public Set<StringCycle> getNCycles(Integer n) {
    return n == 1 ?
             Set.of(StringCycle.builder().elements(List.of(identityDisplay)).build()) :
             Set.of();
  }
  
  @Override
  public Set<StringCycle> getMaximalCycles() {
    return Set.of(StringCycle.builder()
                    .elements(List.of(identityDisplay))
                    .build());
  }
  
  @Override
  public Set<String> getElementsDisplay() {
    return Set.of(identityDisplay);
  }
  
  @Override
  public int getSize() {
    return 1;
  }
  
  @Override
  public String prod(String a, String b) {
    return identityDisplay;
  }
  
  @Override
  public int prod(int a, int b) {
    return 0;
  }
  
  @Override
  public Integer eval(String a) {
    return 0;
  }
  
  @Override
  public String display(int i) {
    return identityDisplay;
  }
  
  @Override
  public int getIdentity() {
    return 0;
  }
}
