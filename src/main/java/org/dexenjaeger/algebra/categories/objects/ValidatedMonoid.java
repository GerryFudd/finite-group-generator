package org.dexenjaeger.algebra.categories.objects;

import lombok.Getter;
import org.dexenjaeger.algebra.model.ValidatedMonoidSpec;
import org.dexenjaeger.algebra.model.ValidatedSemigroupSpec;
import org.dexenjaeger.algebra.categories.morphisms.ValidatingBinaryOperator;

import java.util.List;
import java.util.function.Function;

public class ValidatedMonoid implements Monoid {
  @Getter
  private final String identity;
  private final Semigroup semigroup;
  
  private ValidatedMonoid(String identity, Semigroup semigroup) {
    this.identity = identity;
    this.semigroup = semigroup;
  }
  
  public static ValidatedMonoid createMonoid(
    ValidatedMonoidSpec spec
  ) {
    return createMonoid(
      spec,
      binOp -> ValidatedSemigroup.createSemigroup(new ValidatedSemigroupSpec(
        spec.getOperatorSymbol(), binOp
      ))
    );
  }
  
  public static ValidatedMonoid createMonoid(
    ValidatedMonoidSpec spec,
    Function<ValidatingBinaryOperator, ValidatedSemigroup> makeSemigroup
  ) {
    
    if (!spec.getBinaryOperator().isIdentity(spec.getIdentity())) {
      throw new RuntimeException("Monoids may only be created with valid identity elements.");
    }
    
    return new ValidatedMonoid(
      spec.getIdentity(),
      makeSemigroup.apply(spec.getBinaryOperator())
    );
  }
  
  @Override
  public String getOperatorSymbol() {
    return semigroup.getOperatorSymbol();
  }
  
  @Override
  public List<String> getElementsAsList() {
    return semigroup.getElementsAsList();
  }
  
  @Override
  public String getProduct(String a, String b) {
    return semigroup.getProduct(a, b);
  }
}
