package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
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

class IntCycleTest {
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
    IntCycle cycle = IntCycle.builder()
                       .elements(
                         List.of(elements.split(","))
                           .stream()
                           .map(Integer::parseInt)
                           .collect(Collectors.toList())
                       )
                       .build();
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
    List<Integer> elementList = List.of(elements.split(","))
                                  .stream()
                                  .map(Integer::parseInt)
                                  .collect(Collectors.toList());
    IntCycle cycle = IntCycle.builder()
                       .elements(elementList)
                       .build();
    Set<IntCycle> expectedSubCycles = new HashSet<>();
    List<Integer> subCycleGeneratorList = subCycleGenerators == null ? List.of() : Stream.of(
      subCycleGenerators.split(",")
    ).map(Integer::parseInt).collect(Collectors.toList());
    
    for (Integer subCycleGenerator:subCycleGeneratorList) {
      int i = subCycleGenerator;
      List<Integer> subCycleElements = new ArrayList<>();
      while (i % elementList.size() != 0) {
        subCycleElements.add(elementList.get(i % elementList.size() - 1));
        i += subCycleGenerator;
      }
      subCycleElements.add(elementList.get(elementList.size() - 1));
      expectedSubCycles.add(IntCycle.builder()
                              .elements(subCycleElements)
                              .build());
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
    List<String> elementList = List.of(elements.split(","));
    StringCycle cycle = StringCycle.builder()
                          .elements(elementList)
                          .build();
    Map<Integer, List<String>> subCyclesBySize;
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
                            OrderedPair::getRight
                          ));
    }
    
    for (int i = 1; i <= elementList.size(); i++) {
      List<String> subCycleElements = subCyclesBySize.get(i);
      Optional<StringCycle> subCycle = cycle.getSubCycleOfSize(i);
      
      if (subCycleElements == null) {
        assertTrue(subCycle.isEmpty());
      } else {
        assertEquals(
          StringCycle.builder().elements(subCycleElements).build(),
          subCycle.orElseThrow()
        );
      }
    }
  }
  
  @Test
  void getSubCycleSizesTest() {
    IntCycle cycle = IntCycle.builder()
                       .elements(19, 4, 7, 18, 2, 30)
                       .build();
    
    assertEquals(
      List.of(1, 2, 3),
      cycle.getSubCycleSizes()
    );
  }
  
  @Test
  void getSubCycleOfSizeTest() {
    IntCycle cycle = IntCycle.builder()
                       .elements(19, 4, 7, 18, 2, 30)
                       .build();
    
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
    IntCycle cycle = IntCycle.builder()
                       .elements(19, 4, 7, 18, 2, 30)
                       .build();
    
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
}