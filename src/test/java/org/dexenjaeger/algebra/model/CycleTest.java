package org.dexenjaeger.algebra.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CycleTest {
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
    Cycle cycle = Cycle.builder()
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
  void getSubCyclesTest(String elements, String subcycleGenerators) {
    List<String> elementList = List.of(elements.split(","));
    Cycle cycle = Cycle.builder()
                    .elements(elementList)
                    .build();
    Set<Cycle> expectedSubcycles = new HashSet<>();
    List<Integer> subcycleGeneratorList = subcycleGenerators == null ? List.of() : Stream.of(
      subcycleGenerators.split(",")
    ).map(Integer::parseInt).collect(Collectors.toList());
    
    for (Integer subcycleGenerator:subcycleGeneratorList) {
      int i = subcycleGenerator;
      List<String> subcycleElements = new ArrayList<>();
      while (i % elementList.size() != 0) {
        subcycleElements.add(elementList.get(i % elementList.size() - 1));
        i += subcycleGenerator;
      }
      subcycleElements.add(elementList.get(elementList.size() - 1));
      expectedSubcycles.add(Cycle.builder()
                              .elements(subcycleElements)
                              .build());
    }
    
    assertEquals(
      expectedSubcycles,
      cycle.getSubCycles()
    );
  }
}