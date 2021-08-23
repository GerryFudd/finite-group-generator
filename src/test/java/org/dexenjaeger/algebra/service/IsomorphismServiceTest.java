package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Isomorphism;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.spec.CyclicGroupSpec;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IsomorphismServiceTest {
  private final CycleUtils cycleUtils = new CycleUtils();
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final IsomorphismService isomorphismService = injector.getInstance(IsomorphismService.class);
  
  @Test
  void createIsomorphismTest_domainAndFunc() {
    Map<Integer, Element> functionMap = Map.of(
      0, Element.from("E"),
      1, Element.from("x"),
      2, Element.from("x", 2),
      3, Element.from("x", 3)
    );
    Isomorphism isomorphism = isomorphismService.createIsomorphism(
      groupService.createCyclicGroup("a", 4),
      functionMap::get
    );
    
    assertEquals(
      new HashSet<>(functionMap.values()),
      isomorphism.getRange().getElementsDisplay()
    );
    assertEquals(List.of(1, 2, 4), isomorphism.getRange().getCycleSizes());
    assertEquals(
      Set.of(cycleUtils.createIntCycle(2, 0)),
      isomorphism.getRange().getNCycles(2)
    );
    assertEquals(
      cycleUtils.createSingleIntCycle(1, 2, 3, 0),
      isomorphism.getRange().getMaximalCycles()
    );
    Isomorphism inverseIso = isomorphismService.getInverse(isomorphism);
    assertEquals(
      isomorphism.getDomain(),
      inverseIso.getRange()
    );
    assertEquals(
      isomorphism.getRange(),
      inverseIso.getDomain()
    );
    for (int i = 0; i < 4; i++) {
      assertEquals(i, inverseIso.apply(isomorphism.apply(i)));
      assertEquals(i, isomorphism.apply(inverseIso.apply(i)));
    }
  }
  
  @Test
  void createIsomorphismTest_InvalidDomainAndRange() {
    ValidationException e = assertThrows(ValidationException.class, () -> isomorphismService.createIsomorphism(
      new TrivialGroup(),
      groupService.createCyclicGroup(
        new CyclicGroupSpec()
        .setBase("a")
          .setN(2)
        .setIdentityElement(Element.from("E"))
      ),
      x -> 0,
      y -> 0
    ));
    
    assertEquals(
      "Domain and range are different sizes.", e.getMessage()
    );
  }
  
  @Test
  void createIsomorphismTest_InvalidInverse() {
    ValidationException e = assertThrows(ValidationException.class, () -> isomorphismService.createIsomorphism(
      groupService.createCyclicGroup("a", 2),
      groupService.createCyclicGroup(
        new CyclicGroupSpec()
        .setBase("x")
        .setN(2)
        .setIdentityElement(Element.from("E"))
      ),
      Function.identity(),
      y -> 0
    ));
    
    assertEquals(
      "Function is not a left inverse.", e.getMessage()
    );
  }
  
  @Test
  void createIsomorphismTest_notInjection() {
    ValidationException e = assertThrows(ValidationException.class, () -> isomorphismService.createIsomorphism(
      groupService.createCyclicGroup("a", 2),
      groupService.createCyclicGroup(
        new CyclicGroupSpec()
          .setBase("x")
          .setN(2)
          .setIdentityElement(Element.from("E"))
      ),
      x -> 0,
      Function.identity()
    ));
    
    assertEquals(
      "Kernel is not the inverse image of the identity.", e.getMessage()
    );
  }
}