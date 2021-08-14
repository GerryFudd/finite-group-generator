package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupValidatorTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  @SuppressWarnings("Convert2Diamond") // This encounters an exception if a diamond is used
  private final Validator<Group> groupValidator = injector.getInstance(Key.get(new TypeLiteral<Validator<Group>>(){}));
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  
  @Test
  void invalidInverses() {
    int[][] product = {
      {0, 1, 2, 3},
      {1, 1, 1, 1},
      {2, 2, 2, 2},
      {3, 1, 2, 0}
    };
    
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      4,
      (i, j) -> product[i][j]
    );
    
    Group group = Group.builder()
                    .inversesMap(Map.of(0, 0, 1, 2, 2, 1, 3, 3))
                    .maximalCycles(Set.of(
                      IntCycle.builder().elements(List.of(3, 0)).build(),
                      IntCycle.builder().elements(List.of(1)).build(),
                      IntCycle.builder().elements(List.of(2)).build()
                    ))
                    .lookup(summary.getLookupMap())
                    .operator(summary.getBinaryOperator()::prod)
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertTrue(
      Pattern.compile("The value [ab] is not the inverse of the element [ab] in Group\n\n" +
                        "_\\*____\\|_I____a____b____c____\n" +
                        " I {4}\\| I {4}a {4}b {4}c {4}\n" +
                        " a {4}\\| a {4}a {4}a {4}a {4}\n" +
                        " b {4}\\| b {4}b {4}b {4}b {4}\n" +
                        " c {4}\\| c {4}a {4}b {4}I {4}\n").asMatchPredicate().test(e.getMessage())
    );
  }
  
  @Test
  void inverseNotMemberOfGroup() {
    Group group = Group.builder()
                    .inversesMap(Map.of(0, 0, 1, 2))
                    .maximalCycles(Set.of(IntCycle.builder()
                    .elements(1, 0)
                    .build()))
                    .elements("I", "a")
                    .operator((a, b) -> (a + b) % 2)
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "The inverse of element a not found in Group\n" +
        "\n" +
        "_*____|_I____a____\n" +
        " I    | I    a    \n" +
        " a    | a    I    \n", e.getMessage()
    );
  }
  
  @Test
  void inverseNotPresentInInversesMap() {
    Group group = Group.builder()
                    .inversesMap(Map.of(0, 0))
                    .maximalCycles(Set.of(IntCycle.builder()
                                            .elements(1, 0)
                                            .build()))
                    .elements("I", "a")
                    .operator((a, b) -> (a + b) % 2)
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "The inverse of element a not found in Group\n\n" +
        "_*____|_I____a____\n" +
        " I    | I    a    \n" +
        " a    | a    I    \n", e.getMessage()
    );
  }
}