package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Cycle;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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
    
    Group group = ConcreteGroup.builder()
                    .inversesMap(Map.of(0, 0, 1, 2, 2, 1, 3, 3))
                    .lookup(summary.getLookupMap())
                    .operator(summary.getBinaryOperator()::prod)
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I"), List.of("a"), List.of("b")),
                      2, Set.of(List.of("c", "I"))
                    ))
                    .maximalCycles(Set.of(
                      Cycle.builder().elements(List.of("c", "I")).build(),
                      Cycle.builder().elements(List.of("a")).build(),
                      Cycle.builder().elements(List.of("b")).build()
                    ))
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
    Group group = ConcreteGroup.builder()
                    .elements("I", "a")
                    .inversesMap(Map.of(0, 0, 1, 2))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      2, Set.of(List.of("a", "I"))
                    ))
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
    Group group = ConcreteGroup.builder()
                    .elements("I", "a")
                    .displayInversesMap(Map.of("I", "I"))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      2, Set.of(List.of("a", "I"))
                    ))
                    .displayOperator((a, b) -> a.equals(b) ? "I" : "a")
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "The inverse of element a not found in Group\n\n" +
        "_*____|_I____a____\n" +
        " I    | I    a    \n" +
        " a    | a    I    \n", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapIsEmpty() {
    Group group = ConcreteGroup.builder()
                    .elements("I")
                    .displayInversesMap(Map.of("I", "I"))
                    .displayOperator((a, b) -> "I")
                    .cyclesMap(Map.of())
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: map is empty.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsNullSet() {
    Map<Integer, Set<List<String>>> cyclesMap = new HashMap<>();
    cyclesMap.put(1, null);
    Group group = ConcreteGroup.builder()
                    .elements("I")
                    .displayInversesMap(Map.of("I", "I"))
                    .displayOperator((a, b) -> "I")
                    .cyclesMap(cyclesMap)
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: there exists a null set of nCycles.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsEmptySet() {
    Group group = ConcreteGroup.builder()
                    .elements("I")
                    .displayInversesMap(Map.of("I", "I"))
                    .displayOperator((a, b) -> "I")
                    .cyclesMap(Map.of(1, Set.of()))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: the set of 1 cycles is empty.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsCycleOfTheWrongSize() {
    Group group = ConcreteGroup.builder()
                    .elements("I")
                    .displayInversesMap(Map.of("I", "I"))
                    .displayOperator((a, b) -> "I")
                    .cyclesMap(Map.of(1, Set.of(List.of())))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycle: cycle is empty.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsInvalidCycleSize() {
    Group group = ConcreteGroup.builder()
                    .elements("I")
                    .displayInversesMap(Map.of("I", "I"))
                    .displayOperator((a, b) -> "I")
                    .cyclesMap(Map.of(0, Set.of(List.of())))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: cycle size 0 is invalid.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsCycleThatDoesntEndWithI() {
    Group group = ConcreteGroup.builder()
                    .elements("I", "a")
                    .inversesMap(Map.of(0, 0, 1, 1))
                    .operator((a, b) -> (a + b) % 2)
                    .cyclesMap(Map.of(1, Set.of(List.of("a"))))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycle: cycle [a] doesn't end with identity.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycle() {
    Group group = ConcreteGroup.builder()
                    .elements("I", "a", "b")
                    .inversesMap(Map.of(0, 0, 1, 2, 2, 1))
                    .operator((a, b) -> (a + b) % 3)
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      4, Set.of(List.of("a", "I", "b", "I"))
                    ))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycle: cycle [a, I, b, I] is improperly generated.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycleWithoutInverseAtEnd() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Group group = ConcreteGroup.builder()
                    .elements(elements)
                    .inversesMap(Map.of(0, 0, 1, 5, 2, 4, 3, 3, 4, 2, 5, 1))
                    .operator((a, b) -> (a + b) % 6)
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      5, Set.of(List.of("a", "b", "c", "e", "I"))
                    ))
                    .maximalCycles(Set.of(
                      Cycle.builder()
                        .elements(List.of("a", "b", "c", "e", "I"))
                        .build()))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycle: cycle [a, b, c, e, I] doesn't contain the inverse of each element.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycleWithoutInverseInMiddle() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Group group = ConcreteGroup.builder()
                    .elements(elements)
                    .inversesMap(Map.of(0, 0, 1, 5, 2, 4, 3, 3, 4, 2, 5, 1))
                    .operator((a, b) -> (a + b) % 6)
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      4, Set.of(List.of("a", "b", "e", "I"))
                    ))
                    .maximalCycles(Set.of(
                      Cycle.builder()
                        .elements(List.of("a", "b", "e", "I"))
                        .build()))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycle: cycle [a, b, e, I] doesn't contain the inverse of each element.", e.getMessage()
    );
  }
}