package org.dexenjaeger.algebra.categories.morphisms;

import lombok.Getter;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

import java.util.Arrays;

public class ConcreteHomomorphism implements Homomorphism {
  @Getter
  protected final Group domain;
  @Getter
  protected final Group range;
  @Getter
  protected final Group kernel;
  protected final int[] mapping;
  protected final Element[] image;
  
  ConcreteHomomorphism(
    Group domain,
    Group range,
    Group kernel,
    int[] mapping,
    Element[] image
  ) {
    this.domain = domain;
    this.range = range;
    this.kernel = kernel;
    this.mapping = mapping;
    this.image = image;
  }
  
  @Override
  public int apply(int i) {
    return mapping[i];
  }
  
  @Override
  public Element apply(Element x) {
    return range.display(mapping[domain.eval(x)]);
  }
  
  @Override
  public int hashCode() {
    return 29 * domain.hashCode()
             + Arrays.hashCode(mapping)
             + Arrays.hashCode(image);
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Homomorphism)
          || !domain.equals(((Homomorphism) other).getDomain())
    ) {
      return false;
    }
    
    for (int i = 0; i < domain.getSize(); i++) {
      if (mapping[i] != ((Homomorphism) other).apply(i)) {
        return false;
      }
      if (!image[i].equals(((Homomorphism) other)
                 .getRange()
                 .display(mapping[i]))) {
        return false;
      }
    }
    return true;
  }
}
