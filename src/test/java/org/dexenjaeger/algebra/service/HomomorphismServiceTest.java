package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HomomorphismServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final HomomorphismService homomorphismService = injector.getInstance(HomomorphismService.class);
  private final IsomorphismService isomorphismService = injector.getInstance(IsomorphismService.class);
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  
  @Test
  void createHomomorphismTest() {
    Homomorphism validatedHomomorphism = homomorphismService.createHomomorphism(
      new TrivialGroup(),
      a -> "E"
    );
    
    assertEquals(
      "I", validatedHomomorphism.getDomain().getIdentityDisplay()
    );
    
    assertEquals(
      validatedHomomorphism.getDomain().printMultiplicationTable(),
      validatedHomomorphism.getKernel().printMultiplicationTable()
    );
    
    assertEquals(
      Set.of("E"), validatedHomomorphism.getRange().getElementsDisplay()
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidHomomorphismFunction() {
    assertThrows(
      ValidationException.class,
      () -> homomorphismService.createHomomorphism(
        groupService.createCyclicGroup("I", "a", "b"),
        (a) -> a == 2 ? "B" : "E"
      )
    );
  }
  
  @Test
  void createHomomorphismTest_InvalidKernel() {
    Map<String, Integer> lookup =
      Map.of("I", 0, "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
    
    String[] rangeElements = {"E", "C"};
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> homomorphismService.createHomomorphism(
        groupService.createCyclicGroup(
          "I", "a", "b", "c", "d", "e"
        ),
        groupService.createCyclicGroup(
          "E", "C"
        ),
        groupService.createCyclicGroup(
          "I", "d", "e"
        ),
        i -> i % 2
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
      () -> homomorphismService.createHomomorphism(
        groupService.createCyclicGroup(elements),
        new TrivialGroup("E"),
        groupService.constructGroupFromElementsAndMultiplicationTable(
          elements, kernelMult
        ),
        i -> 0
      )
    );
    
    assertEquals(
      "Kernel binary operator doesn't match group binary operator.", e.getMessage()
    );
  }
  
  @Test
  void trivialHomomorphismCreatesKernelThatEqualsGroup() {
    Group group = groupService.createCyclicGroup(
      "I", "a", "b"
    );
    Homomorphism result = homomorphismService.createHomomorphism(
      group, (a) -> "E"
    );
    
    assertEquals(
      group.printMultiplicationTable(),
      result.getKernel().printMultiplicationTable()
    );
  }
  
  @Test
  void functionImageOutsideRange() {
    Group domain = groupService.createCyclicGroup("I", "a");
    ValidationException e = assertThrows(ValidationException.class, () -> homomorphismService.createHomomorphism(
      domain,
      new TrivialGroup("I"),
      domain,
      Function.identity()
    ));
    
    assertEquals(
      "Range with size 1 doesn't contain image 1 of 1.", e.getMessage()
    );
  }
  
  @Test
  void functionImageNotGroup() {
    String[] rangeElements = {"I", "a", "b"};
    ValidationException e = assertThrows(ValidationException.class, () -> homomorphismService.createHomomorphism(
      groupService.createCyclicGroup("I", "a"),
      groupService.createCyclicGroup(rangeElements, "x"),
      new TrivialGroup("I"),
      Function.identity()
    ));
    
    assertEquals(
      "Function is not a homomorphism, f(1)xf(1)=2, but f(1*1)=0.", e.getMessage()
    );
  }
  
  @Test
  void createHomomorphismTest_createsRange() {
    Group s3 = symmetryGroupGenerator.createSymmetryGroup(3);
    Function<Integer, String> act = a -> {
      if (s3.display(a).equals("d")) {
        return "f2";
      }
      if (s3.display(a).equals("d2")) {
        return "f";
      }
      return s3.display(a);
    };
    
    Homomorphism homomorphism = homomorphismService.createHomomorphism(
      s3, act
    );
    
    Group range = homomorphism.getRange();
    Group kernel = homomorphism.getKernel();
    
    assertEquals(
      new TrivialGroup(),
      kernel
    );
  
    assertEquals(
      4,
      range.getMaximalCycles().size()
    );
  
    assertEquals(
      1,
      range.getNCycles(1).size()
    );
  
    assertEquals(
      3,
      range.getNCycles(2).size()
    );
  
    assertEquals(
      1,
      range.getNCycles(3).size()
    );
  }
}