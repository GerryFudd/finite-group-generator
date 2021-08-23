package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.cycle.ElementCycle;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CycleUtilsTest {
  private final CycleUtils cycleUtils = new CycleUtils();
  @ParameterizedTest
  @CsvSource(
    value = {
      "13:13", "13,4:13", "13,4,5:13,4", "13,4,5,19:13,5",
      "13,4,5,19,45:13,4,5,19", "13,4,5,19,45,71:13,45",
      "13,4,5,19,45,71,18,54:13,5,45,18",
      "13,4,5,19,45,71,18,54,29:13,4,19,45,18,54",
      "13,4,5,19,45,71,18,54,29,92:13,5,18,29"
    },
    delimiter = ':'
  )
  void getGeneratorsTest(String elements, String generators) {
    IntCycle cycle = cycleUtils.convertToIntCycle(
      Integer::parseInt,
      elements.split(",")
    );
    assertEquals(
      Set.of(generators.split(","))
        .stream()
        .map(Integer::parseInt)
        .collect(Collectors.toSet()),
      cycle.getGenerators()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "13:", "13,4:0", "13,4,5:0",
      "13,4,5,19:2,0", "13,4,5,19,45:0",
      "13,4,5,19,45,71:2,3,0",
      "13,4,5,19,45,71,18,54:2,4,0",
      "13,4,5,19,45,71,18,54,29:3,0",
      "13,4,5,19,45,71,18,54,29,92:2,5,0"
    },
    delimiter = ':'
  )
  void getSubCyclesTest(String elements, String subCycleGenerators) {
    IntCycle cycle = cycleUtils.convertToIntCycle(
      Integer::parseInt,
      elements.split(",")
    );
    Set<IntCycle> expectedSubCycles = new HashSet<>();
    List<Integer> subCycleGeneratorList = subCycleGenerators == null ? List.of() : Stream.of(
      subCycleGenerators.split(",")
    ).map(Integer::parseInt).collect(Collectors.toList());
    
    for (Integer subCycleGenerator:subCycleGeneratorList) {
      int i = subCycleGenerator;
      List<Integer> subCycleElements = new ArrayList<>();
      while (i % cycle.getSize() != 0) {
        subCycleElements.add(cycle.get(i % cycle.getSize() - 1));
        i += subCycleGenerator;
      }
      subCycleElements.add(cycle.get(cycle.getSize() - 1));
      expectedSubCycles.add(cycleUtils.createIntCycle(subCycleElements));
    }
    
    assertEquals(
      expectedSubCycles,
      cycle.getSubCycles()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "13:", "13,4:1|4", "13,4,5:1|5", "13,4,5,19:2|4,19;1|19",
      "13,4,5,19,45:1|45", "13,4,5,19,45,71:3|4,19,71;2|5,71;1|71",
      "13,4,5,19,45,71,18,54:4|4,19,71,54;2|19,54;1|54",
      "13,4,5,19,45,71,18,54,29:3|5,71,29;1|29",
      "13,4,5,19,45,71,18,54,29,92:5|4,19,71,54,92;2|45,92;1|92"
    },
    delimiter = ':'
  )
  void getSubCycleOfSizeTest(String elements, String subCycleGenerators) {
    IntCycle cycle = cycleUtils.convertToIntCycle(
      Integer::parseInt,
      elements.split(",")
    );
    Map<Integer, List<Integer>> subCyclesBySize;
    if (subCycleGenerators == null) {
      subCyclesBySize = Map.of();
    } else {
      subCyclesBySize = Stream.of(subCycleGenerators.split(";"))
                          .map(str -> {
                            String[] parts = str.split("\\|");
          
                            return new OrderedPair<>(
                              Integer.parseInt(parts[0]),
                              Stream.of(parts[1].split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList())
                            );
                          })
                          .collect(Collectors.toMap(
                            OrderedPair::getLeft,
                            OrderedPair::getRight
                          ));
    }
    
    for (int i = 1; i <= cycle.getSize(); i++) {
      List<Integer> subCycleElements = subCyclesBySize.get(i);
      Optional<IntCycle> subCycle = cycle.getSubCycleOfSize(i);
      
      if (subCycleElements == null) {
        assertTrue(subCycle.isEmpty());
      } else {
        assertEquals(
          cycleUtils.createIntCycle(subCycleElements),
          subCycle.orElseThrow()
        );
      }
    }
  }
  
  @Test
  void getSubCycleSizesTest() {
    IntCycle cycle = cycleUtils.createIntCycle(19, 4, 7, 18, 2, 30);
    
    assertEquals(
      List.of(1, 2, 3),
      cycle.getSubCycleSizes()
    );
  }
  
  @Test
  void getSubCycleOfSizeTest() {
    IntCycle cycle = cycleUtils.createIntCycle(19, 4, 7, 18, 2, 30);
    
    assertEquals(
      List.of(30),
      cycle.getSubCycleOfSize(1).orElseThrow().getElements()
    );
    
    assertEquals(
      List.of(7, 30),
      cycle.getSubCycleOfSize(2).orElseThrow().getElements()
    );
    
    assertEquals(
      List.of(4, 18, 30),
      cycle.getSubCycleOfSize(3).orElseThrow().getElements()
    );
    
    assertTrue(cycle.getSubCycleOfSize(4).isEmpty());
  }
  
  @Test
  void getSubCycleGeneratedByTest() {
    IntCycle cycle = cycleUtils.createIntCycle(19, 4, 7, 18, 2, 30);
    
    assertEquals(
      List.of(30),
      cycle.getSubCycleGeneratedBy(30).orElseThrow().getElements()
    );
    
    assertEquals(
      List.of(7, 30),
      cycle.getSubCycleGeneratedBy(7).orElseThrow().getElements()
    );
    
    assertEquals(
      List.of(4, 18, 30),
      cycle.getSubCycleGeneratedBy(4).orElseThrow().getElements()
    );
    
    assertEquals(
      List.of(2, 18, 7, 4, 19, 30),
      cycle.getSubCycleGeneratedBy(2)
        .orElseThrow().getElements()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "a:a", "a,b:a", "a,b,c:a,b", "a,b,c,d:a,c",
      "a,b,c,d,e:a,b,c,d", "a,b,c,d,e,f:a,e",
      "a,b,c,d,e,f,g,h:a,c,e,g",
      "a,b,c,d,e,f,g,h,i:a,b,d,e,g,h",
      "a,b,c,d,e,f,g,h,i,j:a,c,g,i"
    },
    delimiter = ':'
  )
  void getGeneratorsTest_String(String elements, String generators) {
    ElementCycle cycle = cycleUtils.createElementCycle(
      Stream.of(elements.split(","))
      .map(Element::from)
      .collect(Collectors.toList())
    );
    assertEquals(
      Stream.of(generators.split(","))
        .map(Element::from)
        .collect(Collectors.toSet()),
      cycle.getGenerators()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "a:", "a,b:0", "a,b,c:0", "a,b,c,d:2,0",
      "a,b,c,d,e:0", "a,b,c,d,e,f:2,3,0",
      "a,b,c,d,e,f,g,h:2,4,0",
      "a,b,c,d,e,f,g,h,i:3,0",
      "a,b,c,d,e,f,g,h,i,j:2,5,0"
    },
    delimiter = ':'
  )
  void getSubCyclesTest_String(String elements, String subCycleGenerators) {
    List<Element> elementList = Stream.of(elements.split(","))
      .map(Element::from)
      .collect(Collectors.toList());
    ElementCycle cycle = cycleUtils.createElementCycle(elementList);
    Set<ElementCycle> expectedSubCycles = new HashSet<>();
    List<Integer> subCycleGeneratorList = subCycleGenerators == null ? List.of() : Stream.of(
      subCycleGenerators.split(",")
    ).map(Integer::parseInt).collect(Collectors.toList());
    
    for (Integer subCycleGenerator:subCycleGeneratorList) {
      int i = subCycleGenerator;
      List<Element> subCycleElements = new ArrayList<>();
      while (i % elementList.size() != 0) {
        subCycleElements.add(elementList.get(i % elementList.size() - 1));
        i += subCycleGenerator;
      }
      subCycleElements.add(elementList.get(elementList.size() - 1));
      expectedSubCycles.add(cycleUtils.createElementCycle(subCycleElements));
    }
    
    assertEquals(
      expectedSubCycles,
      cycle.getSubCycles()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "a:", "a,b:1|b", "a,b,c:1|c", "a,b,c,d:2|b,d;1|d",
      "a,b,c,d,e:1|e", "a,b,c,d,e,f:3|b,d,f;2|c,f;1|f",
      "a,b,c,d,e,f,g,h:4|b,d,f,h;2|d,h;1|h",
      "a,b,c,d,e,f,g,h,i:3|c,f,i;1|i",
      "a,b,c,d,e,f,g,h,i,j:5|b,d,f,h,j;2|e,j;1|j"
    },
    delimiter = ':'
  )
  void getSubCycleOfSizeTest_String(String elements, String subCycleGenerators) {
    List<Element> elementList = Stream.of(elements.split(","))
      .map(Element::from)
      .collect(Collectors.toList());
    ElementCycle cycle = cycleUtils.createElementCycle(elementList);
    Map<Integer, List<Element>> subCyclesBySize;
    if (subCycleGenerators == null) {
      subCyclesBySize = Map.of();
    } else {
      subCyclesBySize = Stream.of(subCycleGenerators.split(";"))
                          .map(str -> {
                            String[] parts = str.split("\\|");
          
                            return new OrderedPair<>(
                              Integer.parseInt(parts[0]),
                              List.of(parts[1].split(","))
                            );
                          })
                          .collect(Collectors.toMap(
                            OrderedPair::getLeft,
                            op -> op.getRight().stream()
                            .map(Element::from)
                            .collect(Collectors.toList())
                          ));
    }
    
    for (int i = 1; i <= elementList.size(); i++) {
      List<Element> subCycleElements = subCyclesBySize.get(i);
      Optional<ElementCycle> subCycle = cycle.getSubCycleOfSize(i);
      
      if (subCycleElements == null) {
        assertTrue(subCycle.isEmpty());
      } else {
        assertEquals(
          cycleUtils.createElementCycle(subCycleElements),
          subCycle.orElseThrow()
        );
      }
    }
  }
  
  @Test
  void getSubCycleSizesTest_String() {
    ElementCycle cycle = cycleUtils.createElementCycle(
      Stream.of("a", "b", "c", "d", "e", "f")
      .map(Element::from)
      .toArray(Element[]::new)
    );
    
    assertEquals(
      List.of(1, 2, 3),
      cycle.getSubCycleSizes()
    );
  }
  
  @Test
  void getSubCycleOfSizeTest_String() {
    ElementCycle cycle = cycleUtils.createElementCycle(
      Stream.of("a", "b", "c", "d", "e", "f")
      .map(Element::from)
      .toArray(Element[]::new)
    );
    
    assertEquals(
      List.of(Element.from("f")),
      cycle.getSubCycleOfSize(1).orElseThrow().getElements()
    );
    
    assertEquals(
      Stream.of("c", "f")
        .map(Element::from)
        .collect(Collectors.toList()),
      cycle.getSubCycleOfSize(2).orElseThrow().getElements()
    );
    
    assertEquals(
      Stream.of("b", "d", "f")
      .map(Element::from)
      .collect(Collectors.toList()),
      cycle.getSubCycleOfSize(3).orElseThrow().getElements()
    );
    
    assertTrue(cycle.getSubCycleOfSize(4).isEmpty());
  }
  
  @Test
  void getSubCycleGeneratedByTest_String() {
    ElementCycle cycle = cycleUtils.createElementCycle(
      Stream.of("a", "b", "c", "d", "e", "f")
      .map(Element::from)
      .collect(Collectors.toList())
    );
    
    assertEquals(
      List.of(Element.from("f")),
      cycle.getSubCycleGeneratedBy(Element.from("f")).orElseThrow().getElements()
    );
    
    assertEquals(
      Stream.of("c", "f")
      .map(Element::from)
      .collect(Collectors.toList()),
      cycle.getSubCycleGeneratedBy(Element.from("c")).orElseThrow().getElements()
    );
    
    assertEquals(
      Stream.of("b", "d", "f")
      .map(Element::from)
      .collect(Collectors.toList()),
      cycle.getSubCycleGeneratedBy(Element.from("b")).orElseThrow().getElements()
    );
    
    assertEquals(
      Stream.of("e", "d", "c", "b", "a", "f")
      .map(Element::from)
      .collect(Collectors.toList()),
      cycle.getSubCycleGeneratedBy(Element.from("e"))
        .orElseThrow().getElements()
    );
  }
}
