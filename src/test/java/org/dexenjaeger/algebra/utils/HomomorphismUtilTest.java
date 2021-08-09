package org.dexenjaeger.algebra.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.service.GroupService;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class HomomorphismUtilTest {
  private final Injector injector = Guice.createInjector();
  private final GroupService groupService = injector.getInstance(GroupService.class);
  
  @Test
  void createHomomorphismTest() throws ValidationException {
    Homomorphism validatedHomomorphism = HomomorphismUtil.createHomomorphism(
      new TrivialGroup(),
      a -> "E"
    );
    
    assertEquals(
      "I", validatedHomomorphism.getDomain().getIdentity()
    );
    
    assertEquals(
      validatedHomomorphism.getDomain().getMultiplicationTable(),
      validatedHomomorphism.getKernel().getMultiplicationTable()
    );
    
    assertEquals(
      Set.of("E"), validatedHomomorphism.getRange().getElements()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidHomomorphismFunction() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> HomomorphismUtil.createHomomorphism(
        groupService.getCyclicGroup("I", "a", "b"),
        (a) -> "b".equals(a) ? "B" : "E"
      )
    );
    
    assertEquals(
      "Invalid homomorphism. Kernel is not a subgroup since a is in the kernel but its inverse b is not.",
      e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernel() {
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    String[] rangeElements = {"E", "C"};
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> HomomorphismUtil.createHomomorphism(
        groupService.getCyclicGroup(
          "I", "a", "b", "c", "d", "e"
        ),
        groupService.getCyclicGroup(
          "E", "C"
        ),
        groupService.getCyclicGroup(
          "I", "d", "e"
        ),
        (a) -> rangeElements[lookup.get(a) % 2]
      )
    );
    
    assertEquals(
      "Kernel is not the inverse image of the identity.", e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernelOperation() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    
    int[][] kernelMult = {
      {0, 1, 2, 3, 4, 5},
      {1, 0, 5, 4, 3, 2},
      {2, 4, 0, 5, 1, 3},
      {3, 5, 4, 0, 2, 1},
      {4, 2, 3, 1, 5, 0},
      {5, 3, 1, 2, 0, 4}
    };
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> HomomorphismUtil.createHomomorphism(
        groupService.getCyclicGroup(elements),
        new TrivialGroup("E"),
        groupService.getGroupFromElementsAndIntTable(
          elements, kernelMult
        ),
        (a) -> "E"
      )
    );
    
    assertEquals(
      "Kernel binary operator doesn't match group binary operator.", e.getMessage()
    );
  }
  
  @Test
  void trivialHomomorphismCreatesKernelThatEqualsGroup() throws ValidationException {
    Group group = groupService.getCyclicGroup(
      "I", "a", "b"
    );
    Homomorphism result = HomomorphismUtil.createHomomorphism(
      group, (a) -> "E"
    );
    
    assertEquals(
      group.getMultiplicationTable(),
      result.getKernel().getMultiplicationTable()
    );
  }
  
  @Test
  void functionImageOutsideRange() {
    Group domain = groupService.getCyclicGroup("I", "a");
    ValidationException e = assertThrows(ValidationException.class, () -> HomomorphismUtil.createHomomorphism(
      domain,
      new TrivialGroup("I"),
      domain,
      Function.identity()
    ));
    
    assertEquals(
      "Range [I] doesn't contain image a of a.", e.getMessage()
    );
  }
  
  @Test
  void functionImageNotGroup() {
    String[] rangeElements = {"I", "a", "b"};
    ValidationException e = assertThrows(ValidationException.class, () -> HomomorphismUtil.createHomomorphism(
      groupService.getCyclicGroup("I", "a"),
      groupService.getCyclicGroup(rangeElements, "x"),
      new TrivialGroup("I"),
      Function.identity()
    ));
    
    assertEquals(
      "Function is not a homomorphism, f(a)xf(a)=b, but f(a*a)=I.", e.getMessage()
    );
  }
  
  @Test
  void createAutomorphismTest_domainAndFunc() throws ValidationException {
    Map<String, String> functionMap = Map.of(
      "I", "E",
      "a", "x",
      "b", "y",
      "c", "z"
    );
    Automorphism automorphism = HomomorphismUtil.createAutomorphism(
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
  }
  
  @Test
  void createAutomorphismTest_InvalidDomainAndRange() {
    ValidationException e = assertThrows(ValidationException.class, () -> HomomorphismUtil.createAutomorphism(
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
    ValidationException e = assertThrows(ValidationException.class, () -> HomomorphismUtil.createAutomorphism(
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
    ValidationException e = assertThrows(ValidationException.class, () -> HomomorphismUtil.createAutomorphism(
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