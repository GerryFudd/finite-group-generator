package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryOperatorServiceTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final BinaryOperatorService binaryOperatorService = injector.getInstance(BinaryOperatorService.class);
  private final GroupService groupService = injector.getInstance(GroupService.class);
  
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withLeftIdentity_fromMappings() {
    BinaryOperatorSummary result = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      List.of(
        new Mapping(new int[]{1, 0, 0}),
        new Mapping(new int[]{0, 1, 1}),
        new Mapping(new int[]{1, 0, 1}),
        new Mapping(new int[]{0, 1, 0})
      )
    );
    
    assertEquals(
      "L, L2, a, b",
      String.join(", ", result.getElements())
    );
    
    assertNull(result.getIdentityDisplay());
    assertNull(result.getInversesMap());
  }
  
  @Test
  void getSortedAndPrettifiedBinaryOperatorTest_withIdentityAndInverses() {
    BinaryOperatorSummary result = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      List.of(
        new Mapping(new int[]{0, 1, 2, 3}),
        new Mapping(new int[]{0, 1, 3, 2}),
        new Mapping(new int[]{1, 0, 2, 3}),
        new Mapping(new int[]{1, 0, 3, 2})
      )
    );
    
    assertEquals(
      "I, a, b, c",
      String.join(", ", result.getElements())
    );
    
    assertEquals("I", result.getIdentityDisplay());
    assertEquals(
      Map.of(0, 0, 1, 1, 2, 2, 3, 3),
      result.getInversesMap()
    );
  }
  
  @Test
  void validInverses() throws ValidationException {
    BinaryOperatorSummary summary = binaryOperatorService.getSortedAndPrettifiedBinaryOperator(
      List.of(
        new Mapping(new int[]{0, 1, 2, 3}),
        new Mapping(new int[]{1, 2, 3, 0}),
        new Mapping(new int[]{2, 3, 0, 1}),
        new Mapping(new int[]{3, 0, 1, 2})
      )
    );
    
    Group result = groupService.createGroup(
      "*", 0,
      summary.getElements(),
      summary.getInversesMap(),
      summary.getCycles(),
      summary.getOperator()
    );
    
    result.getElementsDisplay().forEach(element -> assertEquals(
      result.getIdentityDisplay(),
      result.prod(
        element,
        result.getInverse(element)
      )
    ));
    
    for (int i = 0; i < result.getSize(); i++) {
      assertEquals(
        result.getIdentity(),
        result.prod(
          i,
          result.getInverse(i)
        )
      );
    }
  }
  
  @Test
  void getBinaryOperatorTest_invalidProduct() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> binaryOperatorService
              .createBinaryOperator(new String[]{"a"}, (a, b) -> 1)
    );
    
    assertEquals(
      "The product of a and a doesn't exist in this binary operator\n\n" +
        "_*____|_a____\n" +
        " a    | [1?] \n", e.getMessage()
    );
  }
  
  @Test
  void getMultiplicationTableTest() throws ValidationException {
    assertEquals(
      new StringBuilder("\n")
        .append("_+_|_a_b_c_\n")
        .append(" a | a b c \n")
        .append(" b | b c a \n")
        .append(" c | c a b \n")
        .toString(),
      binaryOperatorService.createBinaryOperator(
        "+", new String[]{"a", "b", "c"},
        (i, j) -> (i + j) % 3
      ).printMultiplicationTable()
    );
  }
  
  @Test
  void validateBinaryOperator() {
    ValidationException e = assertThrows(ValidationException.class, () -> binaryOperatorService.createBinaryOperator(
      new String[]{"a"}, (a, b) -> 1
    ));
    
    assertEquals(
      "The product of a and a doesn't exist in this binary operator\n\n" +
        "_*____|_a____\n" +
        " a    | [1?] \n", e.getMessage()
    );
  }
}