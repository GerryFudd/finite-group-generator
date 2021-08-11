package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
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
  
  private BinaryOperatorSummary semigroupPath(RawBinaryOperatorSummary summary, Remapper remapper, BinaryOperatorSummary.BinaryOperatorSummaryBuilder resultBuilder, BiFunction<Integer, Integer, Integer> binOp) {
    // From here on there is no identity
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
    
    
    Set<Integer> available = new HashSet<>(remapper.getAvailable());
    available.forEach(remapper::map);
    return resultBuilder
             .binaryOperator(remapper.createBinaryOperator(binOp))
             .build();
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
    Map<Integer, Integer> inversesMap = new HashMap<>();
    
    if (identity.isEmpty()) {
      return semigroupPath(summary, remapper, resultBuilder, binOp);
    }
    
    // From here on we may assume that there is an identity.
    inversesMap.put(0, 0);
    resultBuilder.identityDisplay("I")
      .inversesMap(inversesMap);
    remapper.map("I", identity.get()).orElseThrow();
    
    Set<StringCycle> cycles = new HashSet<>();
    for (IntCycle intCycle:summary.getCycles()) {
      List<String> resultCycleElements = new LinkedList<>();
      LinkedList<Integer> indexCycle = new LinkedList<>();
      indexCycle.addLast(remapper.getCurrentIndex());
      String baseValue = remapper.map(intCycle.get(0)).orElseThrow();
      resultCycleElements.add(baseValue);
      int i = 1;
      while (i < intCycle.getSize() - 1) {
        indexCycle.addLast(remapper.getCurrentIndex());
        resultCycleElements.add(remapper.map(
          baseValue + (i + 1),
          intCycle.get(i)
        ).orElseThrow());
        i++;
      }
      
      if (identity.get().equals(intCycle.get(intCycle.getSize() - 1))) {
        resultCycleElements.add("I");
        while (!indexCycle.isEmpty()) {
          Integer valIndex = indexCycle.removeFirst();
          Integer invIndex = indexCycle.isEmpty() ? valIndex : indexCycle.removeLast();
    
          inversesMap.put(valIndex, invIndex);
          inversesMap.put(invIndex, valIndex);
        }
      }
      
      cycles.add(StringCycle.builder().elements(resultCycleElements).build());
    }
    if (!remapper.getAvailable().isEmpty()) {
      throw new RuntimeException("All permutations should exist in some cycle.");
    }
    return resultBuilder
             .binaryOperator(remapper.createBinaryOperator(binOp))
             .cycles(cycles)
             .build();
  }
}
