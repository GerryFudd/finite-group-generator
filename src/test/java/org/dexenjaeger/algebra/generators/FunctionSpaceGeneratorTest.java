package org.dexenjaeger.algebra.generators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.service.MonoidService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionSpaceGeneratorTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final FunctionSpaceGenerator functionSpaceGenerator = injector.getInstance(FunctionSpaceGenerator.class);
  private final MonoidService monoidService = injector.getInstance(MonoidService.class);
  @Test
  void  generatesTheTrivialMonoid() {
    assertEquals(
      new TrivialGroup(),
      functionSpaceGenerator.createFunctionSpace(1)
    );
  }
  @Test
  void  generatesTheCorrectSetOf2Functions() {
    assertEquals(
      monoidService.createMonoid(
        0, new Element[]{
          Element.I, Element.from("a"), Element.from("b"), Element.from("c")
        },
        (i, j) -> new int[][]{
          {0, 1, 2, 3},
          {1, 1, 1, 1},
          {2, 2, 2, 2},
          {3, 2, 1, 0}
        }[i][j]
      ),
      functionSpaceGenerator.createFunctionSpace(2)
    );
  }
}