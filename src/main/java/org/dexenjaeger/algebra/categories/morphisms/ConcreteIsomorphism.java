package org.dexenjaeger.algebra.categories.morphisms;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

public class ConcreteIsomorphism extends ConcreteHomomorphism implements Isomorphism {
  protected final int[] inverseMapping;
  
  ConcreteIsomorphism(
    Group domain,
    Group range,
    int[] mapping,
    Element[] image,
    int[] inverseMapping
  ) {
    super(
      domain, range,
      new TrivialGroup(domain.getIdentityDisplay()),
      mapping, image
    );
    this.inverseMapping = inverseMapping;
  }
  
  @Override
  public int unApply(int j) {
    return inverseMapping[j];
  }
  
  @Override
  public Element unApply(Element j) {
    return domain.display(inverseMapping[range.eval(j)]);
  }
}
