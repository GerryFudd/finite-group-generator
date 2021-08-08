package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.function.Function;

@Builder
public class ConcreteAutomorphism implements Automorphism {
  @Getter
  private final Group domain;
  @Getter
  private final Group range;
  private final Function<String, String> act;
  private final Function<String, String> inverseAct;
  
  @Override
  public Automorphism getInverse() {
    return ConcreteAutomorphism.builder()
             .range(range)
             .domain(domain)
             .act(inverseAct)
             .inverseAct(act)
             .build();
  }
  
  @Override
  public String apply(String a) {
    return act.apply(a);
  }
  
  @Override
  public String unApply(String b) {
    return inverseAct.apply(b);
  }
}
