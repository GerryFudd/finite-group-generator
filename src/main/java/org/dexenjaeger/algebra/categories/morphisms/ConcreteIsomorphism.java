package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.function.Function;

@Builder
public class ConcreteIsomorphism implements Isomorphism {
  @Getter
  private final Group domain;
  @Getter
  private final Group range;
  private final Function<Integer, Integer> act;
  private final Function<Integer, Integer> inverseAct;
  
  @Override
  public Isomorphism getInverse() {
    return ConcreteIsomorphism.builder()
             .range(range)
             .domain(domain)
             .act(inverseAct)
             .inverseAct(act)
             .build();
  }
  
  @Override
  public int apply(int a) {
    return act.apply(a);
  }
  
  @Override
  public int unApply(int b) {
    return inverseAct.apply(b);
  }
}
