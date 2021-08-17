package org.dexenjaeger.algebra.categories.objects.group;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.service.GroupService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrivialGroupTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  @Test
  void equalsItself() {
    assertEquals(
      new TrivialGroup(),
      new TrivialGroup()
    );
  }
  
  @Test
  void doesNotEqualIfIdentityDiffers() {
    assertNotEquals(
      new TrivialGroup("a"),
      new TrivialGroup("b")
    );
  }
  
  @Test
  void equalsEquivalentBinaryOperator() {
    BinaryOperator byAnotherName = binaryOperatorService.createBinaryOperator(
      new String[]{"a"}, (i, j) -> 0
    );
    assertEquals(
      byAnotherName,
      new TrivialGroup("a")
    );
    assertEquals(
      new TrivialGroup("a"),
      byAnotherName
    );
    assertEquals(
      byAnotherName.hashCode(),
      new TrivialGroup("a").hashCode()
    );
  }
  
  @Test
  void behavesLikeEquivalentGroup() {
    TrivialGroup trivialGroup = new TrivialGroup("a");
    Group byAnotherName = groupService.createCyclicGroup("a");
    assertEquals(
      byAnotherName.getSize(),
      trivialGroup.getSize()
    );
    assertEquals(
      byAnotherName.getIdentity(),
      trivialGroup.getIdentity()
    );
    assertEquals(
      byAnotherName.getIdentityDisplay(),
      trivialGroup.getIdentityDisplay()
    );
    assertEquals(
      byAnotherName.getElementsDisplay(),
      trivialGroup.getElementsDisplay()
    );
    assertEquals(
      byAnotherName.display(0),
      trivialGroup.display(0)
    );
    assertEquals(
      byAnotherName.getInverse(0),
      trivialGroup.getInverse(0)
    );
    assertEquals(
      byAnotherName.getCycleSizes(),
      trivialGroup.getCycleSizes()
    );
    assertEquals(
      byAnotherName.getNCycles(1),
      trivialGroup.getNCycles(1)
    );
    assertEquals(
      byAnotherName.getNCycleGenerators(1),
      trivialGroup.getNCycleGenerators(1)
    );
    assertEquals(
      byAnotherName.getNCycles(2),
      trivialGroup.getNCycles(2)
    );
  }
}