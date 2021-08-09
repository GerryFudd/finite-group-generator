package org.dexenjaeger.algebra;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.monoid.Monoid;
import org.dexenjaeger.algebra.categories.objects.semigroup.Semigroup;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.validators.BinaryOperatorValidator;
import org.dexenjaeger.algebra.validators.GroupValidator;
import org.dexenjaeger.algebra.validators.HomomorphismValidator;
import org.dexenjaeger.algebra.validators.MonoidValidator;
import org.dexenjaeger.algebra.validators.SemigroupValidator;
import org.dexenjaeger.algebra.validators.Validator;

public class AlgebraModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Validator<BinaryOperator>>(){})).to(BinaryOperatorValidator.class);
    bind(Key.get(new TypeLiteral<Validator<Semigroup>>(){})).to(SemigroupValidator.class);
    bind(Key.get(new TypeLiteral<Validator<Monoid>>(){})).to(MonoidValidator.class);
    bind(Key.get(new TypeLiteral<Validator<Group>>(){})).to(GroupValidator.class);
    bind(Key.get(new TypeLiteral<Validator<Homomorphism>>(){})).to(HomomorphismValidator.class);
  }
}
