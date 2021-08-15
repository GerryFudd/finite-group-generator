package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutomorphismServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final AutomorphismService automorphismService = injector.getInstance(AutomorphismService.class);
  private final GroupService groupService = injector.getInstance(GroupService.class);
  
  @Test
  void createAutomorphism() throws ValidationException {
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
}
