package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.utils.RawBinaryOperatorSummary;
import org.dexenjaeger.algebra.utils.Remapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class BinaryOperatorService {
  public BiFunction<String, String, String> createOperator(
    String[] elements, BiFunction<Integer, Integer, Integer> intOp
  ) {
    Map<String, Integer> lookup = new HashMap<>();
    for (int i = 0; i < elements.length; i++) {
      lookup.put(elements[i], i);
    }
    return (a, b) -> elements[intOp.apply(lookup.get(a), lookup.get(b))];
  }
  
  public BinaryOperatorSummary getSortedAndPrettifiedBinaryOperator(
    int size,
    BiFunction<Integer, Integer, Integer> binOp
  ) {
    Remapper remapper = Remapper.init(size);
    BinaryOperatorSummary.BinaryOperatorSummaryBuilder resultBuilder = BinaryOperatorSummary.builder()
      .lookupMap(remapper.getReverseLookup());
    
    RawBinaryOperatorSummary summary = new RawBinaryOperatorSummary();
    for (int i = 0; i < size; i++) {
      boolean isLeftIdentity = true;
      boolean isRightIdentity = true;
      for (int j = 0; j < size; j++) {
        isLeftIdentity = isLeftIdentity && binOp.apply(i, j) == j;
        isRightIdentity = isRightIdentity && binOp.apply(j, i) == j;
      }
      if (isLeftIdentity) {
        summary.getLeftIdentities().add(i);
      }
      if (isRightIdentity) {
        summary.getRightIdentities().add(i);
      }
      
      List<Integer> cycle = new LinkedList<>();
      cycle.add(i);
      int next = binOp.apply(i, i);
      while (!cycle.contains(next)) {
        cycle.add(next);
        next = binOp.apply(i, next);
      }
      summary.addCycle(cycle);
    }
    Optional<Integer> identity = summary.getIdentity();
    Map<String, String> displayInverseMap = new HashMap<>();
    Map<Integer, Integer> inversesMap = new HashMap<>();
    if (identity.isPresent()) {
      displayInverseMap.put("I", "I");
      inversesMap.put(0, 0);
      resultBuilder.identityDisplay("I")
        .displayInversesMap(displayInverseMap)
        .inversesMap(inversesMap);
      remapper.map("I", identity.get()).orElseThrow();
    } else {
      int l = 1;
      int r = 1;
      for (int leftIdentity:summary.getLeftIdentities()) {
        if (remapper.map("L" + l, leftIdentity).isPresent()) {
          l++;
        }
      }
      for (int rightIdentity:summary.getRightIdentities()) {
        if (remapper.map("R" + r, rightIdentity).isPresent()) {
          r++;
        }
      }
    }
    Map<Integer, Set<List<String>>> cyclesMap = new HashMap<>();
    for (int cycleSize: summary.getCycleSizes()) {
      summary.getNCycles(cycleSize).forEach(cycle -> {
        cyclesMap.computeIfAbsent(cycleSize, (acc) -> new HashSet<>());
        List<String> resultCycle = new LinkedList<>();
        LinkedList<Integer> indexCycle = new LinkedList<>();
        indexCycle.addLast(remapper.getCurrentIndex());
        String baseValue = remapper.map(cycle.get(0)).orElseThrow();
        resultCycle.add(baseValue);
        int i = 1;
        while (i < cycle.size() - 1) {
          indexCycle.addLast(remapper.getCurrentIndex());
          resultCycle.add(remapper.map(baseValue + (i + 1), cycle.get(i)).orElseThrow());
          i++;
        }
        
        LinkedList<String> cycleVals = new LinkedList<>(resultCycle);
        resultCycle.add("I");
        cyclesMap.get(cycleSize).add(resultCycle);
        while (!cycleVals.isEmpty()) {
          String val = cycleVals.removeFirst();
          String inv = cycleVals.isEmpty() ? val : cycleVals.removeLast();
          displayInverseMap.put(val, inv);
          displayInverseMap.put(inv, val);
          
          Integer valIndex = indexCycle.removeFirst();
          Integer invIndex = indexCycle.isEmpty() ? valIndex : indexCycle.removeLast();
          inversesMap.put(valIndex, invIndex);
          inversesMap.put(invIndex, valIndex);
        }
      });
    }
    resultBuilder.cyclesMap(cyclesMap);
    if (!remapper.getAvailable().isEmpty()) {
      throw new RuntimeException("All permutations should exist in some cycle.");
    }
    return resultBuilder
             .binaryOperator(remapper.createBinaryOperator(binOp))
             .build();
  }
}
