package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.utils.HomomorphismUtil;

import java.util.function.Function;

public class ValidatedAutomorphism implements Automorphism {
  private final Group domain;
  private final Group range;
  private final Function<String, String> act;
  private final Function<String, String> inverseAct;
  
  private ValidatedAutomorphism(
    Group domain,
    Group range,
    Function<String, String> act,
    Function<String, String> inverseAct
  ) {
    this.domain = domain;
    this.range = range;
    this.act = act;
    this.inverseAct = inverseAct;
  }
  
  public static ValidatedAutomorphism createAutomorphism(
    ValidatedHomomorphism homomorphism,
    Function<String, String> inverseAct
  ) {
    return doCreateAutomorphism(
      homomorphism.getDomain(),
      homomorphism.getRange(),
      homomorphism::apply,
      inverseAct
    );
  }
  
  private static ValidatedAutomorphism doCreateAutomorphism(
    Group domain,
    Group range,
    Function<String, String> act,
    Function<String, String> inverseAct
  ) {
    HomomorphismUtil.validateHomomorphism(domain, range, act);
    HomomorphismUtil.validateBijection(domain.getElementsAsList(), range.getElementsAsList(), act);
    HomomorphismUtil.validateLeftInverse(domain.getElementsAsList(), act, inverseAct);
    
    return new ValidatedAutomorphism(
      domain, range, act, inverseAct
    );
  }
  
  @Override
  public Automorphism getInverse() {
    return new ValidatedAutomorphism(
      range,
      domain,
      inverseAct,
      act
    );
  }
  
  @Override
  public Group getDomain() {
    return domain;
  }
  
  @Override
  public Group getRange() {
    return range;
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
