package org.dexenjaeger.algebra.categories.morphisms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.model.cycle.ElementCycle;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CyclePresentation {
  @EqualsAndHashCode.Include
  @Getter
  private final List<ElementCycle> cycles;
  
  @Override
  public String toString() {
    if (cycles.size() == 0) {
      return "I";
    }
    return cycles.stream()
      .map(ElementCycle::toString)
      .collect(Collectors.joining());
  }
}
