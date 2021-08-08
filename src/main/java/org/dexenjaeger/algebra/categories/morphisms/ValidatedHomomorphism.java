package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.SafeGroup;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.HomomorphismUtil;

import java.util.function.Function;

public class ValidatedHomomorphism implements Homomorphism {
  @Getter
  private final Group domain;
  @Getter
  private final Group range;
  @Getter
  private final Group kernel;
  private final Function<String, String> act;
  
  private ValidatedHomomorphism(
    Group domain,
    Group range,
    Group kernel,
    Function<String, String> act
  ) {
    this.domain = domain;
    this.range = range;
    this.kernel = kernel;
    this.act = act;
  }
  
  public static ValidatedHomomorphism createHomomorphism(
    SafeGroup domain, SafeGroup range, SafeGroup kernel, Function<String, String> act
  ) {
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  private static ValidatedHomomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<String, String> act
  ) {
    HomomorphismUtil.validateHomomorphism(domain, range, act);
    
    HomomorphismUtil.validateInverseImageOfId(domain, kernel, range.getIdentity(), act);
    
    BinaryOperatorUtil.validateSubgroup(domain, kernel);
    
    return new ValidatedHomomorphism(
      domain, range, kernel, act
    );
  }
  
  public static ValidatedHomomorphism createHomomorphism(
    SafeGroup domain,
    Function<String, String> act
  ) {
    OrderedPair<Group, Group> rangeAndKernel = HomomorphismUtil.constructRangeAndKernel(
      domain, act
    );
    
    return doCreateHomomorphism(
      domain, rangeAndKernel.getLeft(), rangeAndKernel.getRight(), act
    );
  }
  
  @Override
  public String apply(String in) {
    return act.apply(in);
  }
}
