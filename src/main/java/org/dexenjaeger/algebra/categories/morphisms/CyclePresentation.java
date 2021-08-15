package org.dexenjaeger.algebra.categories.morphisms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.cycle.StringCycle;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CyclePresentation {
  @EqualsAndHashCode.Include
  @Getter(AccessLevel.PROTECTED)
  private final List<StringCycle> cycles;
  
  @Override
  public String toString() {
    if (cycles.size() == 0) {
      return "I";
    }
    return cycles.stream()
      .map(StringCycle::toString)
      .collect(Collectors.joining());
  }
}
