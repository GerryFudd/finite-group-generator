package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.categories.objects.group.TrivialGroup;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutomorphismServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final AutomorphismService automorphismService = injector.getInstance(AutomorphismService.class);
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  
  @Test
  void createAutomorphism() {
    Automorphism automorphism = automorphismService.createAutomorphism(
      groupService.createCyclicGroup("I", "a", "b", "c"),
      i -> (3 * i) % 4
    );
    
    assertEquals(
      "(ac)",
      automorphism.toString()
    );
    
    assertEquals(
      automorphism,
      automorphismService.getInverse(automorphism)
    );
  }
  
  @Test
  void createAutomorphism_notMonomorphism() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> automorphismService.createAutomorphism(
      groupService.createCyclicGroup("I", "a", "b", "c"),
      i -> (2 * i) % 4
    )
    );
    
    assertEquals(
      "Kernel is not the inverse image of the identity.",
      e.getMessage()
    );
  }
  
  @Test
  void createAutomorphismGroupForTrivialGroup() {
    Group result = automorphismService.createAutomorphismGroup(new TrivialGroup());
    
    assertEquals(
      new TrivialGroup(), result
    );
  }
  
  @Test
  void createAutomorphismGroupForS2() {
    Group result = automorphismService.createAutomorphismGroup(symmetryGroupGenerator.createSymmetryGroup(2));
    
    assertEquals(
      new TrivialGroup(), result
    );
  }
  
  @Test
  void createAutomorphismGroupForCyclicGroup3() {
    Group result = automorphismService.createAutomorphismGroup(groupService.createCyclicGroup(
      "I", "a", "b"
    ));
    
    assertEquals(
      groupService.createGroup(
        new GroupSpec()
        .setIdentity(0)
        .setElements(new String[]{"I", "(ab)"})
        .setOperator((i, j) -> (i + j) % 2)
      ),
      result
    );
  }
  
  @Test
  void createAutomorphismGroupForS3() {
    Group result = automorphismService.createAutomorphismGroup(symmetryGroupGenerator.createSymmetryGroup(3));
    
    assertEquals(
      groupService.createGroup(
        new GroupSpec()
        .setIdentity(0)
        .setElements(new String[]{"I", "(ab)(dd2)", "(ac)(dd2)", "(bc)(dd2)", "(abc)", "(acb)"})
        .setOperator((i, j) -> new int[][]{
          {0, 1, 2, 3, 4, 5},
          {1, 0, 5, 4, 3, 2},
          {2, 4, 0, 5, 1, 3},
          {3, 5, 4, 0, 2, 1},
          {4, 2, 3, 1, 5, 0},
          {5, 3, 1, 2, 0, 4}
        }[i][j])
      ),
      result
    );
  }
}
