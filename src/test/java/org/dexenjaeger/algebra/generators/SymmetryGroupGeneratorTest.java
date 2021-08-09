package org.dexenjaeger.algebra.generators;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymmetryGroupGeneratorTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  
  @Test
  void createS2() {
    Group s2 = symmetryGroupGenerator.createSymmetryGroup(2);
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_o____|_I____a____\n")
      .append(" I    | I    a    \n")
      .append(" a    | a    I    \n");
    
    assertEquals(
      sb.toString(),
      s2.getMultiplicationTable()
    );
  }
  
  @Test
  void createS3() {
    Group s3 = symmetryGroupGenerator.createSymmetryGroup(3);
    StringBuilder sb = new StringBuilder();
    sb.append("\n")
      .append("_o____|_I____a____b____c____d____d2___\n")
      .append(" I    | I    a    b    c    d    d2   \n")
      .append(" a    | a    I    d2   d    c    b    \n")
      .append(" b    | b    d    I    d2   a    c    \n")
      .append(" c    | c    d2   d    I    b    a    \n")
      .append(" d    | d    b    c    a    d2   I    \n")
      .append(" d2   | d2   c    a    b    I    d    \n");
    
    assertEquals(
      sb.toString(),
      s3.getMultiplicationTable()
    );
  }
  
  @Test
  void createS4() {
    Group s4 = symmetryGroupGenerator.createSymmetryGroup(4);
    
    Set<String> elements = s4.getElements();
    Map<Integer, Set<String>> groupOrderCount = new HashMap<>();
    for (String element:elements) {
      List<String> cycle = s4.getCycle(element);
      groupOrderCount.compute(cycle.size(), (key, set) -> {
        if (set == null) {
          set = new HashSet<>();
        }
        set.add(element);
        return set;
      });
    }
    
    assertEquals(
      1,
      groupOrderCount.get(1).size()
    );
    assertEquals(
      9,
      groupOrderCount.get(2).size()
    );
    assertEquals(
      8,
      groupOrderCount.get(3).size()
    );
    assertEquals(
      6,
      groupOrderCount.get(4).size()
    );
    assertEquals(
      groupOrderCount.values().stream()
        .map(Set::size)
        .reduce(0, Integer::sum),
      elements.size()
    );
  }
}