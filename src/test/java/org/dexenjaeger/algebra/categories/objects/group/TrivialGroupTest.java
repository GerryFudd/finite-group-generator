package org.dexenjaeger.algebra.categories.objects.group;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.spec.CyclicGroupSpec;
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
      new TrivialGroup(Element.from("a")),
      new TrivialGroup(Element.from("b"))
    );
  }
  
  @Test
  void equalsEquivalentBinaryOperator() {
    BinaryOperator byAnotherName = binaryOperatorService.createBinaryOperator(
      new Element[]{Element.from("a")}, (i, j) -> 0
    );
    assertEquals(
      byAnotherName,
      new TrivialGroup(Element.from("a"))
    );
    assertEquals(
      new TrivialGroup(Element.from("a")),
      byAnotherName
    );
    assertEquals(
      byAnotherName.hashCode(),
      new TrivialGroup(Element.from("a")).hashCode()
    );
  }
  
  @Test
  void behavesLikeEquivalentGroup() {
    TrivialGroup trivialGroup = new TrivialGroup(Element.from("a"));
    Group byAnotherName = groupService.createCyclicGroup(
      new CyclicGroupSpec()
        .setIdentityElement(Element.from("a"))
    );
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