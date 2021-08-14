package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InnerAutomorphismServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final InnerAutomorphismService innerAutomorphismService = injector.getInstance(InnerAutomorphismService.class);
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  @Test
  void correctlyGeneratesAnInnerAutomorphism() {
    Group s3 = symmetryGroupGenerator.createSymmetryGroup(3);
    
    s3.getElementsDisplay().stream()
      .map(s3::eval)
      .forEach(i -> {
        Automorphism automorphism = innerAutomorphismService.createInnerAutomorphism(s3, i);
        // Either i is the identity, or this automorphism only
        // fixes the cycle that i belongs to.
        if (i == s3.getIdentity()) {
          assertEquals(
            automorphism,
            automorphism.getInverse()
          );
        } else {
          assertEquals(
            s3.getCycleGeneratedBy(i).orElseThrow().getElementsSet(),
            automorphism.fixedElements()
          );
        }
      });
  }
}