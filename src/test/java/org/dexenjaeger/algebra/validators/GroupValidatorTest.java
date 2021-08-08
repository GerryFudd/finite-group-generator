package org.dexenjaeger.algebra.validators;

import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupValidatorTest {
  private final Validator<Group> groupValidator = new GroupValidator(new MonoidValidator(new SemigroupValidator(new BinaryOperatorValidator())));
  @Test
  void invalidInverses() {
    int[][] product = {
      {0, 1, 2, 3},
      {1, 1, 1, 1},
      {2, 2, 2, 2},
      {3, 1, 2, 0}
    };
    
    BinaryOperatorSummary summary = BinaryOperatorUtil.getSortedAndPrettifiedBinaryOperator(
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
}