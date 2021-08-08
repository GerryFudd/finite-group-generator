package org.dexenjaeger.algebra.categories.objects.monoid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;

import java.util.Set;

@RequiredArgsConstructor
public class UnsafeMonoid implements Monoid {
  @Getter
  private final String identity;
  private final Semigroup semigroup;
  
  @Override
  public String getOperatorSymbol() {
    return semigroup.getOperatorSymbol();
  }
  
  @Override
  public Set<String> getElements() {
    return semigroup.getElements();
  }
  
  @Override
  public String prod(String a, String b) {
    return semigroup.prod(a, b);
  }
}
