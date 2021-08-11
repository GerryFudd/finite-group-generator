package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Builder;
import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.function.Function;

@Builder
public class ConcreteHomomorphism implements Homomorphism {
  @Getter
  private final Group domain;
  @Getter
  private final Group range;
  @Getter
  private final Group kernel;
  private final Function<Integer, Integer> act;
  
  @Override
  public int apply(int in) {
    return act.apply(in);
  }
}
