package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.ConcreteAutomorphism;
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
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  
  @Test
  void createHomomorphismTest() throws ValidationException {
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
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> homomorphismService.createHomomorphism(
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
      () -> homomorphismService.createHomomorphism(
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
      () -> homomorphismService.createHomomorphism(
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
    Group domain = groupService.getCyclicGroup("I", "a");
    ValidationException e = assertThrows(ValidationException.class, () -> homomorphismService.createHomomorphism(
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
    ValidationException e = assertThrows(ValidationException.class, () -> homomorphismService.createHomomorphism(
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
  void createHomomorphismTest_createsRange() throws ValidationException {
    Group s3 = symmetryGroupGenerator.createSymmetryGroup(3);
    Function<String, String> act = a -> {
      if (a.equals("d")) {
        return "d2";
      }
      if (a.equals("d2")) {
        return "d";
      }
      return a;
    };
    
    Homomorphism homomorphism = homomorphismService.createHomomorphism(
      s3, act
    );
    
    Automorphism result = ConcreteAutomorphism.builder()
                            .domain(homomorphism.getDomain())
                            .range(homomorphism.getRange())
                            .act(act)
                            .inverseAct(act)
                            .build();
    
    assertEquals(
      "\n" +
        "_x____|_I____a____b____c____d____d2___\n" +
        " I    | I    a    b    c    d    d2   \n" +
        " a    | a    I    d    d2   b    c    \n" +
        " b    | b    d2   I    d    c    a    \n" +
        " c    | c    d    d2   I    a    b    \n" +
        " d    | d    c    a    b    d2   I    \n" +
        " d2   | d2   b    c    a    I    d    \n",
      result.getRange().printMultiplicationTable()
    );
  }
}