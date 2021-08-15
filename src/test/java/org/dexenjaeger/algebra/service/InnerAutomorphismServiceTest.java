package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.validators.ValidationException;
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
            automorphism.getDomain().getSize(),
            automorphism.getFixedElements().size()
          );
        } else {
          assertEquals(
            s3.getCycleGeneratedBy(i).orElseThrow().getElementsSet(),
            automorphism.getFixedElements()
          );
        }
      });
  }
  
  @Test
  void createInnerAutomorphismGroup() throws ValidationException {
    Group inn3 = innerAutomorphismService.createInnerAutomorphismGroup(
      symmetryGroupGenerator.createSymmetryGroup(3)
    );
    
    assertEquals(
      "\n" +
        "_o_________|_I_________(ab)(dd2)_(ac)(dd2)_(bc)(dd2)_(abc)_____(acb)_____\n" +
        " I         | I         (ab)(dd2) (ac)(dd2) (bc)(dd2) (abc)     (acb)     \n" +
        " (ab)(dd2) | (ab)(dd2) I         (acb)     (abc)     (bc)(dd2) (ac)(dd2) \n" +
        " (ac)(dd2) | (ac)(dd2) (abc)     I         (acb)     (ab)(dd2) (bc)(dd2) \n" +
        " (bc)(dd2) | (bc)(dd2) (acb)     (abc)     I         (ac)(dd2) (ab)(dd2) \n" +
        " (abc)     | (abc)     (ac)(dd2) (bc)(dd2) (ab)(dd2) (acb)     I         \n" +
        " (acb)     | (acb)     (bc)(dd2) (ab)(dd2) (ac)(dd2) I         (abc)     \n",
      inn3.toString()
    );
  }
}