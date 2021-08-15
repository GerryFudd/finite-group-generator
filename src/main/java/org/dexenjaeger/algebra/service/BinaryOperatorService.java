package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.cycle.AbstractCycle;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.RawBinaryOperatorSummary;
import org.dexenjaeger.algebra.utils.Remapper;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BinaryOperatorService {
  private final Validator<BinaryOperator> binaryOperatorValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  
  @Inject
  public BinaryOperatorService(
    Validator<BinaryOperator> binaryOperatorValidator,
    BinaryOperatorUtil binaryOperatorUtil
  ) {
    this.binaryOperatorValidator = binaryOperatorValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
  }
  
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
             .elements(remapper.getElements())
             .operator(remapper.remapBiFunc(binOp))
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
    
    for (IntCycle intCycle:summary
                             .getCycles()
                             .stream()
                             .sorted(
                               Comparator.comparing(
                                 AbstractCycle::getSize
                               )
                             )
                             .collect(
                               Collectors.toList()
                             )
    ) {
      LinkedList<Integer> indexCycle = new LinkedList<>();
      indexCycle.addLast(remapper.getCurrentIndex());
      String baseValue = remapper.map(intCycle.get(0)).orElseThrow();
      int i = 1;
      while (i < intCycle.getSize() - 1) {
        indexCycle.addLast(remapper.getCurrentIndex());
        remapper.map(
          baseValue + (i + 1),
          intCycle.get(i)
        ).orElseThrow();
        i++;
      }
      
      int last = intCycle.get(intCycle.getSize() - 1);
      if (identity.get().equals(last)) {
        while (!indexCycle.isEmpty()) {
          Integer valIndex = indexCycle.removeFirst();
          Integer invIndex = indexCycle.isEmpty() ? valIndex : indexCycle.removeLast();
          
          inversesMap.put(valIndex, invIndex);
          inversesMap.put(invIndex, valIndex);
        }
      } else {
        remapper.map(last);
      }
    }
    if (!remapper.getAvailable().isEmpty()) {
      throw new RuntimeException("All permutations should exist in some cycle.");
    }
    return resultBuilder
             .elements(remapper.getElements())
             .operator(remapper.remapBiFunc(binOp))
             .cycles(remapper.remapCycles(summary.getCycles()))
             .build();
  }
  
  public BinaryOperator createBinaryOperator(
    String[] elements, BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    return createBinaryOperator("*", elements, operator);
  }
  
  public BinaryOperator createBinaryOperator(
    String operatorSymbol, String[] elements, BiFunction<Integer, Integer, Integer> operator
  ) throws ValidationException {
    
    BinaryOperator result = BinaryOperator.builder()
                              .operatorSymbol(operatorSymbol)
                              .size(elements.length)
                              .elements(elements)
                              .lookup(binaryOperatorUtil.createLookup(elements))
                              .multiplicationTable(binaryOperatorUtil.getMultiplicationTable(
                                elements.length, operator
                              ))
                              .build();
    binaryOperatorValidator.validate(result);
    return result;
  }
}
