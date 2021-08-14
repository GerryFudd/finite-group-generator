package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Isomorphism;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
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
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final IsomorphismService isomorphismService = injector.getInstance(IsomorphismService.class);
  
  @Test
  void createIsomorphismTest_domainAndFunc() throws ValidationException {
    Map<Integer, String> functionMap = Map.of(
      0, "E",
      1, "x",
      2, "y",
      3, "z"
    );
    Isomorphism isomorphism = isomorphismService.createIsomorphism(
      groupService.getCyclicGroup("I", "a", "b", "c"),
      functionMap::get
    );
    
    assertEquals(
      new HashSet<>(functionMap.values()),
      isomorphism.getRange().getElementsDisplay()
    );
    assertEquals(List.of(1, 2, 4), isomorphism.getRange().getCycleSizes());
    assertEquals(
      Set.of(IntCycle.builder().elements(2, 0).build()),
      isomorphism.getRange().getNCycles(2)
    );
    assertEquals(
      Set.of(IntCycle.builder()
               .elements(1, 2, 3, 0)
               .build()),
      isomorphism.getRange().getMaximalCycles()
    );
    Isomorphism inverseIso = isomorphism.getInverse();
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
      new TrivialGroup("I"),
      groupService.getCyclicGroup("E", "a"),
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
      groupService.getCyclicGroup("I", "a"),
      groupService.getCyclicGroup("E", "x"),
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
      groupService.getCyclicGroup("I", "a"),
      groupService.getCyclicGroup("E", "x"),
      x -> 0,
      Function.identity()
    ));
    
    assertEquals(
      "Kernel is not the inverse image of the identity.", e.getMessage()
    );
  }
}