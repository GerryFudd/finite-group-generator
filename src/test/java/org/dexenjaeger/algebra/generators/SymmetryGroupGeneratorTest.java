package org.dexenjaeger.algebra.generators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymmetryGroupGeneratorTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  private final BinaryOperatorUtil binaryOperatorUtil = injector.getInstance(BinaryOperatorUtil.class);
  
  @Test
  void createS2() {
    Group s2 = symmetryGroupGenerator.createSymmetryGroup(2);
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_o_|_I_a_\n")
      .append(" I | I a \n")
      .append(" a | a I \n");
    
    assertEquals(
      sb.toString(),
      s2.printMultiplicationTable()
    );
  }
  
  @Test
  void createS3() {
    Group s3 = symmetryGroupGenerator.createSymmetryGroup(3);
    
    assertEquals(
      4,
      s3.getMaximalCycles().size()
    );
    
    assertEquals(
      1,
      s3.getNCycles(1).size()
    );
    
    assertEquals(
      3,
      s3.getNCycles(2).size()
    );
    
    assertEquals(
      1,
      s3.getNCycles(3).size()
    );
  }
  
  @Test
  void createS4() {
    Group s4 = symmetryGroupGenerator.createSymmetryGroup(4);
    
    assertEquals(
      4 * 3 * 2,
      s4.getSize()
    );
    List<Integer> cycleSizes = s4.getCycleSizes();
    assertEquals(
      List.of(1, 2, 3, 4),
      cycleSizes
    );
    
    assertEquals(
      1,
      s4.getNCycles(1).size()
    );
    assertEquals(
      9,
      s4.getNCycles(2).size()
    );
    assertEquals(
      4,
      s4.getNCycles(3).size()
    );
    assertEquals(
      3,
      s4.getNCycles(4).size()
    );
  }
}