package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.model.Cycle;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutomorphismServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final AutomorphismService automorphismService = injector.getInstance(AutomorphismService.class);
  
  @Test
  void createAutomorphismTest_domainAndFunc() throws ValidationException {
    Map<String, String> functionMap = Map.of(
      "I", "E",
      "a", "x",
      "b", "y",
      "c", "z"
    );
    Automorphism automorphism = automorphismService.createAutomorphism(
      groupService.getCyclicGroup("I", "a", "b", "c"),
      functionMap::get
    );
    
    assertEquals(
      new HashSet<>(functionMap.values()),
      automorphism.getRange().getElements()
    );
    assertEquals(List.of(1, 4), automorphism.getRange().getCycleSizes());
    assertEquals(
      Set.of(List.of("x", "y", "z", "E")),
      automorphism.getRange().getNCycles(4)
    );
    assertEquals(
      Set.of(Cycle.builder()
               .elements(List.of("x", "y", "z", "E"))
               .build()),
      automorphism.getRange().getMaximalCycles()
    );
  }
  
  @Test
  void createAutomorphismTest_InvalidDomainAndRange() {
    ValidationException e = assertThrows(ValidationException.class, () -> automorphismService.createAutomorphism(
      new TrivialGroup("I"),
      groupService.getCyclicGroup("E", "a"),
      x -> "E",
      y -> "I"
    ));
    
    assertEquals(
      "Domain and range are different sizes.", e.getMessage()
    );
  }
  
  @Test
  void createAutomorphismTest_InvalidInverse() {
    ValidationException e = assertThrows(ValidationException.class, () -> automorphismService.createAutomorphism(
      groupService.getCyclicGroup("I", "a"),
      groupService.getCyclicGroup("E", "x"),
      x -> {
        switch(x) {
          case "I":
            return "E";
          case "a":
            return "x";
          default:
            return null;
        }
      },
      y -> "I"
    ));
    
    assertEquals(
      "Function is not a left inverse.", e.getMessage()
    );
  }
  
  @Test
  void createAutomorphismTest_notInjection() {
    ValidationException e = assertThrows(ValidationException.class, () -> automorphismService.createAutomorphism(
      groupService.getCyclicGroup("I", "a"),
      groupService.getCyclicGroup("E", "x"),
      x -> "E",
      y -> "I"
    ));
    
    assertEquals(
      "Kernel is not the inverse image of the identity.", e.getMessage()
    );
  }
}