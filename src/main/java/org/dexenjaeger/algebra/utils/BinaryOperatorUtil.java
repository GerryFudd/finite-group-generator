package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.ValidatedBinaryOperator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class BinaryOperatorUtil {
  
  public static ValidatedBinaryOperator getSortedAndPrettifiedBinaryOperator(
    int size,
    BiFunction<Integer, Integer, Integer> binOp
  ) {
    Remapper remapper = Remapper.init(size);
    
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
    if (identity.isPresent()) {
      remapper.map("I", identity.get()).orElseThrow(() ->
        new RuntimeException("The identity disappeared before it was set.")
      );
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
    for (int cycleSize: summary.getCycleSizes()) {
      summary.getNCycles(cycleSize).forEach(cycle -> {
        Optional<String> baseValue = remapper.map(cycle.get(0));
        if (baseValue.isEmpty()) {
          return;
        }
        int i = 1;
        while (i < cycle.size()) {
          remapper.map(baseValue.get() + (i + 1), cycle.get(i));
          i++;
        }
      });
    }
    remapper.getAvailable().forEach(remapper::map);
    return remapper.createBinaryOperator(binOp);
  }
}
