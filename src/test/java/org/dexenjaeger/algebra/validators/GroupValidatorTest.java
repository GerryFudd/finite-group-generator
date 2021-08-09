package org.dexenjaeger.algebra.validators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.service.BinaryOperatorService;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
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
                    .inversesMap(Map.of("I", "I", "a", "b", "b", "a", "c", "c"))
                    .elements(summary.getBinaryOperator().getElements())
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
    Group group = ConcreteGroup.builder()
                    .elements(Set.of("I", "a"))
                    .inversesMap(Map.of("I", "I", "a", "b"))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      2, Set.of(List.of("a", "I"))
                    ))
                    .operator((a, b) -> a.equals(b) ? "I" : "a")
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "The inverse b of element a not found in Group [I, a]", e.getMessage()
    );
  }
  
  @Test
  void inverseNotPresentInInversesMap() {
    Group group = ConcreteGroup.builder()
                    .elements(Set.of("I", "a"))
                    .inversesMap(Map.of("I", "I"))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      2, Set.of(List.of("a", "I"))
                    ))
                    .operator((a, b) -> a.equals(b) ? "I" : "a")
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
                    .elements(Set.of("I"))
                    .inversesMap(Map.of("I", "I"))
                    .operator((a, b) -> "I")
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
                    .elements(Set.of("I"))
                    .inversesMap(Map.of("I", "I"))
                    .operator((a, b) -> "I")
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
                    .elements(Set.of("I"))
                    .inversesMap(Map.of("I", "I"))
                    .operator((a, b) -> "I")
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
                    .elements(Set.of("I"))
                    .inversesMap(Map.of("I", "I"))
                    .operator((a, b) -> "I")
                    .cyclesMap(Map.of(1, Set.of(List.of())))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: there is a 1 cycle whose length isn't 1.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsInvalidCycleSize() {
    Group group = ConcreteGroup.builder()
                    .elements(Set.of("I"))
                    .inversesMap(Map.of("I", "I"))
                    .operator((a, b) -> "I")
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
                    .elements(Set.of("I", "a"))
                    .inversesMap(Map.of("I", "I", "a", "a"))
                    .operator((a, b) -> a.equals(b) ? "I" : "a")
                    .cyclesMap(Map.of(1, Set.of(List.of("a"))))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: cycle [a] doesn't end with identity.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycle() {
    String[] elements = {"I", "a", "b"};
    Group group = ConcreteGroup.builder()
                    .elements(Set.of(elements))
                    .inversesMap(Map.of("I", "I", "a", "b", "b", "a"))
                    .operator(BinaryOperatorUtil.createOperator(
                      elements, (a, b) -> (a + b) % 3
                    ))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      4, Set.of(List.of("a", "I", "b", "I"))
                    ))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: cycle [a, I, b, I] is improperly generated.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycleWithoutInverseAtEnd() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Group group = ConcreteGroup.builder()
                    .elements(Set.of(elements))
                    .inversesMap(Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"))
                    .operator(BinaryOperatorUtil.createOperator(
                      elements, (a, b) -> (a + b) % 6
                    ))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      5, Set.of(List.of("a", "b", "c", "e", "I"))
                    ))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: cycle [a, b, c, e, I] doesn't contain the inverse of each element.", e.getMessage()
    );
  }
  
  @Test
  void cyclesMapContainsImproperCycleWithoutInverseInMiddle() {
    String[] elements = {"I", "a", "b", "c", "d", "e"};
    Group group = ConcreteGroup.builder()
                    .elements(Set.of(elements))
                    .inversesMap(Map.of("I", "I", "a", "e", "b", "d", "c", "c", "d", "b", "e", "a"))
                    .operator(BinaryOperatorUtil.createOperator(
                      elements, (a, b) -> (a + b) % 6
                    ))
                    .cyclesMap(Map.of(
                      1, Set.of(List.of("I")),
                      4, Set.of(List.of("a", "b", "e", "I"))
                    ))
                    .build();
    
    ValidationException e = assertThrows(ValidationException.class, () -> groupValidator.validate(group));
    
    assertEquals(
      "Invalid cycles map: cycle [a, b, e, I] doesn't contain the inverse of each element.", e.getMessage()
    );
  }
}