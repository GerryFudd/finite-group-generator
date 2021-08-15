package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  private final CycleUtils cycleUtils = injector.getInstance(CycleUtils.class);
  @Test
  void invalidInverses() {
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      List.of(
        new Mapping(new int[]{0, 1}),
        new Mapping(new int[]{0, 0}),
        new Mapping(new int[]{1, 1}),
        new Mapping(new int[]{1, 0})
      )
    );
    
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createSortedGroup(
        summary.getElements(),
        Map.of(0, 0, 1, 2, 2, 1, 3, 3),
        summary.getCycles(),
        summary.getOperator()
      ));
    
    assertTrue(
      Pattern.compile("The value [ab] is not the inverse of the element [ab] in Group\n\n" +
                        "_\\*_\\|_I_a_b_c_\n" +
                        " I \\| I a b c \n" +
                        " a \\| a a a a \n" +
                        " b \\| b b b b \n" +
                        " c \\| c b a I \n").asMatchPredicate().test(e.getMessage()),
      String.format("Expected particular validation message. Instead got\n%s", e.getMessage())
    );
  }
  
  @Test
  void inverseNotMemberOfGroup() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        "*", 0,
        new String[]{"I", "a"},
        Map.of(0, 0, 1, 2),
        Set.of(cycleUtils.createIntCycle(1, 0)),
        (a, b) -> (a + b) % 2
      ));
    
    assertEquals(
      "The inverse of element a not found in inverses map for Group\n" +
        "\n" +
        "_*_|_I_a_\n" +
        " I | I a \n" +
        " a | a I \n", e.getMessage()
    );
  }
  
  @Test
  void inverseNotPresentInInversesMap() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createSortedGroup(
        new String[]{"I", "a"},
        Map.of(0, 0),
        Set.of(cycleUtils.createIntCycle(1, 0)),
        (a, b) -> (a + b) % 2
      ));
    
    assertEquals(
      "The inverse of element a not found in inverses map for Group\n\n" +
        "_*_|_I_a_\n" +
        " I | I a \n" +
        " a | a I \n", e.getMessage()
    );
  }
}