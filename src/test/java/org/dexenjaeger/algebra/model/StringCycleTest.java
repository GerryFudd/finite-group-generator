package org.dexenjaeger.algebra.model;

import org.dexenjaeger.algebra.model.cycle.StringCycle;
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

class StringCycleTest {
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
  void getGeneratorsTest(String elements, String generators) {
    StringCycle cycle = StringCycle.builder()
                    .elements(List.of(elements.split(",")))
                    .build();
    assertEquals(
      Set.of(generators.split(",")),
      cycle.getGenerators()
    );
  }
  
  @ParameterizedTest
  @CsvSource(
    value = {
      "a:", "a,b:2", "a,b,c:3", "a,b,c,d:2,4",
      "a,b,c,d,e:5", "a,b,c,d,e,f:2,3,6",
      "a,b,c,d,e,f,g,h:2,4,8",
      "a,b,c,d,e,f,g,h,i:3,9",
      "a,b,c,d,e,f,g,h,i,j:2,5,10"
    },
    delimiter = ':'
  )
  void getSubCyclesTest(String elements, String subCycleGenerators) {
    List<String> elementList = List.of(elements.split(","));
    StringCycle cycle = StringCycle.builder()
                    .elements(elementList)
                    .build();
    Set<StringCycle> expectedSubCycles = new HashSet<>();
    List<Integer> subCycleGeneratorList = subCycleGenerators == null ? List.of() : Stream.of(
      subCycleGenerators.split(",")
    ).map(Integer::parseInt).collect(Collectors.toList());
    
    for (Integer subCycleGenerator:subCycleGeneratorList) {
      int i = subCycleGenerator;
      List<String> subCycleElements = new ArrayList<>();
      while (i % elementList.size() != 0) {
        subCycleElements.add(elementList.get(i % elementList.size() - 1));
        i += subCycleGenerator;
      }
      subCycleElements.add(elementList.get(elementList.size() - 1));
      expectedSubCycles.add(StringCycle.builder()
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
      "a:", "a,b:1|b", "a,b,c:1|c", "a,b,c,d:2|b,d;1|d",
      "a,b,c,d,e:1|e", "a,b,c,d,e,f:3|b,d,f;2|c,f;1|f",
      "a,b,c,d,e,f,g,h:4|b,d,f,h;2|d,h;1|h",
      "a,b,c,d,e,f,g,h,i:3|c,f,i;1|i",
      "a,b,c,d,e,f,g,h,i,j:5|b,d,f,h,j;2|e,j;1|j"
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
}