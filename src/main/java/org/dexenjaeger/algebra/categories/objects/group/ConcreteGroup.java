package org.dexenjaeger.algebra.categories.objects.group;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.model.Cycle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Builder
public class ConcreteGroup implements Group {
  @Getter
  @Builder.Default
  private final String operatorSymbol = "*";
  
  @Getter
  @Builder.Default
  private final String identity = "I";
  @Getter
  private final Set<String> elements;
  private final Map<String, String> inversesMap;
  private final Map<Integer, Set<List<String>>> cyclesMap;
  private final BiFunction<String, String, String> operator;
  
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
  public Set<Cycle> getMaximalCycles() {
    if (elements.size() == 1) {
      return Set.of(Cycle.builder()
                      .elements(List.of(identity))
                      .build());
    }
    return cyclesMap.entrySet().stream()
      .filter(entry -> entry.getKey() > 1)
      .map(Map.Entry::getValue)
      .flatMap(Set::stream)
      .map(cycleElements -> Cycle.builder()
      .elements(cycleElements)
      .build())
      .collect(Collectors.toSet());
  }
  
  @Override
  public String prod(String a, String b) {
    return operator.apply(a, b);
  }
}
