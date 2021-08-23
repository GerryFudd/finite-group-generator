package org.dexenjaeger.algebra.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
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
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
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
        new GroupSpec()
          .setElements(summary.getElements())
          .setOperator(summary.getOperator())
          .setInversesMap(Map.of(0, 0, 1, 2, 2, 1, 3, 3))
      ));
    
    assertTrue(
      Pattern.compile("The inverse of element [12] not found in inverses map for Group\n\n" +
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
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a")})
          .setInversesMap(Map.of(0, 0, 1, 2))
          .setMaximalCycles(Set.of(cycleUtils.createIntCycle(1, 0)))
          .setOperator((a, b) -> (a + b) % 2)
      ));
    
    assertEquals(
      "The value 2 is not the inverse of the element 1 in Group\n" +
        "\n" +
        "_*_|_I_a_\n" +
        " I | I a \n" +
        " a | a I \n", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCycleMissingInverses() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(2, 0)
          ))
          .setOperator((i, j) -> (i + j) % 3)
      )
    );
    
    assertEquals(
      "Invalid cycle: cycle [2, 0] is improperly generated.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withEmptyCycle() {
    RuntimeException e = assertThrows(
      RuntimeException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(List.of())
          ))
          .setOperator((i, j) -> (i + j) % 3)
      )
    );
    
    assertEquals(
      "Empty cycles are not allowed.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCycleMissingIdentity() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(1, 2)
          ))
          .setOperator((i, j) -> (i + j) % 3)
      )
    );
    
    assertEquals(
      "Invalid cycle: cycle [1, 2] ends prematurely.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCycleMissingStep() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b"), Element.from("c")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(1, 3, 0)
          ))
          .setOperator((i, j) -> (i + j) % 4)
      )
    );
    
    assertEquals(
      "Invalid cycle: cycle [1, 3, 0] is improperly generated.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCycleThatIsOutsideGroup() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b"), Element.from("c")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(4)
          ))
          .setOperator((i, j) -> (i + j) % 4)
      )
    );
    
    assertEquals(
      "Invalid cycle: cycle contains 4, which is outside group.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCyclesThatOverlap() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b"), Element.from("c")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(1, 2, 3, 0),
            cycleUtils.createIntCycle(2, 0)
          ))
          .setOperator((i, j) -> (i + j) % 4)
      )
    );
    
    assertEquals(
      "Invalid cycle: 2 is covered by more than one maximal cycle.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_withCyclesThatDontCoverGroup() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createGroup(
        new GroupSpec()
          .setIdentity(0)
          .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b"), Element.from("c")})
          .setMaximalCycles(Set.of(
            cycleUtils.createIntCycle(2, 0)
          ))
          .setOperator((i, j) -> (i + j) % 4)
      )
    );
    
    assertEquals(
      "Cycles don't cover group.", e.getMessage()
    );
  }
  
  @Test
  void createGroup_isForgivingOfMissingParametersCyclic() {
    Group group = groupService.createGroup(
      new GroupSpec()
        .setIdentity(0)
        .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b")})
        .setOperator((i, j) -> (i + j) % 3)
    );
    
    assertEquals(
      "\n" +
        "_*_|_I_a_b_\n" +
        " I | I a b \n" +
        " a | a b I \n" +
        " b | b I a \n",
      group.toString()
    );
    
    assertEquals(
      0, group.getInverse(0)
    );
    assertEquals(
      2, group.getInverse(1)
    );
    assertEquals(
      1, group.getInverse(2)
    );
    assertEquals(
      Element.I, group.display(0)
    );
    assertEquals(
      Element.from("a"), group.display(1)
    );
    assertEquals(
      Element.from("b"), group.display(2)
    );
    
    assertEquals(
      Set.of(cycleUtils.createIntCycle(1, 2, 0)),
      group.getMaximalCycles()
    );
  }
  
  @Test
  void createGroup_isForgivingOfMissingParametersMoreCycles() {
    Group group = groupService.createGroup(
      new GroupSpec()
        .setIdentity(0)
        .setElements(new Element[]{Element.I, Element.from("a"), Element.from("b"), Element.from("c")})
        .setOperator((i, j) -> new int[][]{
          {0, 1, 2, 3},
          {1, 0, 3, 2},
          {2, 3, 0, 1},
          {3, 2, 1, 0}
        }[i][j])
    );
    
    assertEquals(
      "\n" +
        "_*_|_I_a_b_c_\n" +
        " I | I a b c \n" +
        " a | a I c b \n" +
        " b | b c I a \n" +
        " c | c b a I \n",
      group.toString()
    );
    
    assertEquals(
      0, group.getInverse(0)
    );
    assertEquals(
      1, group.getInverse(1)
    );
    assertEquals(
      2, group.getInverse(2)
    );
    assertEquals(
      3, group.getInverse(3)
    );
    assertEquals(
      Element.I, group.display(0)
    );
    assertEquals(
      Element.from("a"), group.display(1)
    );
    assertEquals(
      Element.from("b"), group.display(2)
    );
    assertEquals(
      Element.from("c"), group.display(3)
    );
    
    assertEquals(
      Set.of(
        cycleUtils.createIntCycle(1, 0),
        cycleUtils.createIntCycle(2, 0),
        cycleUtils.createIntCycle(3, 0)
      ),
      group.getMaximalCycles()
    );
  }
  
  @Test
  void createQuotientGroupTest() {
    Group quotientGroup = groupService.createQuotientGroup(
      symmetryGroupGenerator.createSymmetryGroup(3),
      List.of(
        Element.I, Element.from("d"), Element.from("d", 2)
      )
    );
    
    assertEquals(
      groupService.createCyclicGroup(
        Element.from("[I]"), Element.from("[a]")
      ),
      quotientGroup
    );
  }
  
  @Test
  void createQuotientGroupTest_notNormalSubgroup() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createQuotientGroup(
        symmetryGroupGenerator.createSymmetryGroup(3),
        List.of(Element.I, Element.from("a"))
      )
    );
    
    assertTrue(
      Pattern.matches(
        "The value \\w+ belongs to \\[\\w+H] but not \\[H\\w+] for subgroup H=\\[[^]]+]\\.",
      e.getMessage()
      ),
      String.format(
        "Expected message to match a particular pattern. Instead got\n%s",
        e.getMessage()
        )
    );
  }
  
  @Test
  void createQuotientGroupTest_notSubgroup() {
    ValidationException e = assertThrows(
      ValidationException.class,
      () -> groupService.createQuotientGroup(
        symmetryGroupGenerator.createSymmetryGroup(3),
        List.of(Element.I, Element.from("a"), Element.from("b"))
      )
    );
  
    assertTrue(
      Pattern.matches(
        "The set \\[[^]]+] is not a subgroup\\.",
        e.getMessage()
      ),
      String.format(
        "Expected message to match a particular pattern. Instead got\n%s",
        e.getMessage()
      )
    );
  }
}