package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermutationUtilTest {
  @Test
  void getNextKPermutationTest_onePone() {
    assertEquals(
      new Mapping(new int[]{0}),
      PermutationUtil.getNextPermutation(
        new Mapping(new int[]{}), 0
      )
    );
  }
  
  @Test
  void getNextKPermutationTest_onePFour() {
    assertEquals(
      List.of(
        new Mapping(new int[]{0}),
        new Mapping(new int[]{1}),
        new Mapping(new int[]{2}),
        new Mapping(new int[]{3})
      ),
      List.of(
        PermutationUtil.getNextPermutation(
          new Mapping(new int[]{}), 0
        ),
        PermutationUtil.getNextPermutation(
          new Mapping(new int[]{}), 1
        ),
        PermutationUtil.getNextPermutation(
          new Mapping(new int[]{}), 2
        ),
        PermutationUtil.getNextPermutation(
          new Mapping(new int[]{}), 3
        )
      )
    );
  }
  
  @Test
  void getNextKPermutationTest_twoPFive() {
    List<Mapping> twoPFive = new LinkedList<>();
    
    for (Mapping onePFourEntry:List.of(
      new Mapping(new int[]{0}),
      new Mapping(new int[]{1}),
      new Mapping(new int[]{2}),
      new Mapping(new int[]{3})
    )) {
      for (int i = 0; i < 5; i++) {
        assertTrue(twoPFive.add(PermutationUtil.getNextPermutation(
          onePFourEntry, i
        )));
      }
    }
    assertEquals(
      List.of(
        new Mapping(new int[]{1,0}),
        new Mapping(new int[]{0,1}),
        new Mapping(new int[]{0,2}),
        new Mapping(new int[]{0,3}),
        new Mapping(new int[]{0,4}),
        new Mapping(new int[]{2,0}),
        new Mapping(new int[]{2,1}),
        new Mapping(new int[]{1,2}),
        new Mapping(new int[]{1,3}),
        new Mapping(new int[]{1,4}),
        new Mapping(new int[]{3,0}),
        new Mapping(new int[]{3,1}),
        new Mapping(new int[]{3,2}),
        new Mapping(new int[]{2,3}),
        new Mapping(new int[]{2,4}),
        new Mapping(new int[]{4,0}),
        new Mapping(new int[]{4,1}),
        new Mapping(new int[]{4,2}),
        new Mapping(new int[]{4,3}),
        new Mapping(new int[]{3,4})
      ),
      twoPFive
    );
  }
  
  @Test
  void getNextKPermutationTest_threePSix() {
    List<Mapping> threePSix = new LinkedList<>();
    
    for (Mapping threePSixEntry:List.of(
      new Mapping(new int[]{1,0}),
      new Mapping(new int[]{0,1}),
      new Mapping(new int[]{0,2}),
      new Mapping(new int[]{0,3}),
      new Mapping(new int[]{0,4}),
      new Mapping(new int[]{2,0}),
      new Mapping(new int[]{2,1}),
      new Mapping(new int[]{1,2}),
      new Mapping(new int[]{1,3}),
      new Mapping(new int[]{1,4}),
      new Mapping(new int[]{3,0}),
      new Mapping(new int[]{3,1}),
      new Mapping(new int[]{3,2}),
      new Mapping(new int[]{2,3}),
      new Mapping(new int[]{2,4}),
      new Mapping(new int[]{4,0}),
      new Mapping(new int[]{4,1}),
      new Mapping(new int[]{4,2}),
      new Mapping(new int[]{4,3}),
      new Mapping(new int[]{3,4})
    )) {
      for (int i = 0; i < 6; i++) {
        assertTrue(threePSix.add(PermutationUtil.getNextPermutation(
          threePSixEntry, i
        )));
      }
    }
    // This set is the correct size
    assertEquals(
      6*5*4,
      threePSix.size()
    );
    // Every value is unique
    assertEquals(
      new HashSet<>(threePSix).size(),
      threePSix.size()
    );
    // every element is three values long
    threePSix.forEach(mapping -> assertEquals(
      3, mapping.size()
    ));
    assertTrue(
      threePSix.stream()
        .map(Mapping::getArray)
        .map(Arrays::stream)
        .flatMap(IntStream::boxed)
        .allMatch(val -> 0 <= val && val < 6),
      "no entry should fall outside the range 0 <= i < 6"
    );
  }
  
  @Test
  void getKPermutationIteratorTest_matchesThreePSix() {
    List<Mapping> threePSix = new LinkedList<>();
    for (Mapping x:PermutationUtil.getKPermutationIterable(6, 3)) {
      threePSix.add(x);
    }
    // This set is the correct size
    assertEquals(
      6*5*4,
      threePSix.size()
    );
    // Every value is unique
    assertEquals(
      new HashSet<>(threePSix).size(),
      threePSix.size()
    );
    // every element is three values long
    threePSix.forEach(mapping -> assertEquals(
      3, mapping.size()
    ));
    assertTrue(
      threePSix.stream()
        .map(Mapping::getArray)
        .map(Arrays::stream)
        .flatMap(IntStream::boxed)
        .allMatch(val -> 0 <= val && val < 6),
      "no entry should fall outside the range 0 <= i < 6"
    );
  }
  
  @Test
  void getKPermutationIteratorTest_matchesPermutationsIfKEqualsN() {
    List<Mapping> permutationsOn6 = new LinkedList<>();
    for (Mapping x:PermutationUtil.getKPermutationIterable(6, 6)) {
      permutationsOn6.add(x);
    }
    // This set is the same as what you get from the list
    assertEquals(
      PermutationUtil.getPermutationList(6),
      permutationsOn6
    );
  }
}