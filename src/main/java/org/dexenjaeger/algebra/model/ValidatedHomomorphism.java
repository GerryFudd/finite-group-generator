package org.dexenjaeger.algebra.model;

import lombok.Getter;
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
    Group domain, Group range, Group kernel, Function<String, String> act
  ) {
    
    if (!HomomorphismUtil.isHomomorphism(domain, range, act)) {
      throw new RuntimeException("A Homomorphism must be constructed from a valid homomorphism function.");
    }
    
    if (!HomomorphismUtil.isInverseImageOfId(domain, kernel, range.getIdentity(), act)) {
      throw new RuntimeException("A Homomorphism's kernel must be the inverse image of the range's identity.");
    }
    
    if (!BinaryOperatorUtil.isSubgroup(domain, kernel)) {
      throw new RuntimeException("A Homomorphism's kernel must be a subgroup of the domain.");
    }
    
    return new ValidatedHomomorphism(
      domain, range, kernel, act
    );
  }
  
  public static ValidatedHomomorphism createHomomorphism(
    ValidatedGroupSpec domainSpec,
    Function<ValidatedGroupSpec, ValidatedGroup> validatedGroupConstructor,
    Function<String, String> act
  ) {
    Group domain = validatedGroupConstructor.apply(domainSpec);
    
    OrderedPair<Group, Group> rangeAndKernel = HomomorphismUtil.constructRangeAndKernel(
      domain, act
    );
    
    return createHomomorphism(
      domain, rangeAndKernel.getLeft(), rangeAndKernel.getRight(), act
    );
  }
  
  @Override
  public String apply(String in) {
    return act.apply(in);
  }
}
