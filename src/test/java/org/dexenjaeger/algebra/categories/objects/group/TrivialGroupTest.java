package org.dexenjaeger.algebra.categories.objects.group;

import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrivialGroupTest {
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
    BinaryOperator byAnotherName = BinaryOperator.builder()
                                     .lookup(Map.of("a", 0))
                                     .operator((i, j) -> 0)
                                     .build();
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
    Group byAnotherName = Group.builder()
                            .inversesMap(Map.of(0, 0))
                            .maximalCycles(Set.of(
                              IntCycle.builder()
                              .elements(0)
                              .build()
                            ))
                            .lookup(Map.of("a", 0))
                            .operator((i, j) -> 0)
                            .build();
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