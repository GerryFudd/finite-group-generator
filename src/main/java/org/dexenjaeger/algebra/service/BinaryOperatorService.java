package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.model.BinaryOperatorSummary;
import org.dexenjaeger.algebra.model.Element;
import org.dexenjaeger.algebra.model.Mapping;
import org.dexenjaeger.algebra.model.binaryoperator.BinaryOperator;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.cycle.AbstractCycle;
import org.dexenjaeger.algebra.model.cycle.MappingCycle;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.utils.FunctionsUtil;
import org.dexenjaeger.algebra.utils.MappingUtil;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BinaryOperatorService {
  private final Validator<BinaryOperator> binaryOperatorValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  private final FunctionsUtil functionsUtil;
  private final CycleUtils cycleUtils;
  
  @Inject
  public BinaryOperatorService(
    Validator<BinaryOperator> binaryOperatorValidator,
    BinaryOperatorUtil binaryOperatorUtil,
    FunctionsUtil functionsUtil, CycleUtils cycleUtils
  ) {
    this.binaryOperatorValidator = binaryOperatorValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
    this.functionsUtil = functionsUtil;
    this.cycleUtils = cycleUtils;
  }
  
  private BiFunction<Integer, Integer, Integer> getBinOp(List<Mapping> mappings) {
    return (i, j) -> mappings.indexOf(
      functionsUtil.composeMappings(
        mappings.get(i), mappings.get(j)
      )
    );
  }
  
  private Element[] getElements(List<Mapping> mappings) {
    return mappings.stream()
             .map(Mapping::getDisplay)
             .toArray(Element[]::new);
  }
  
  public BinaryOperatorSummary getSortedAndPrettifiedBinaryOperator(
    List<Mapping> mappings
  ) {
    MappingUtil mappingUtil = MappingUtil.init(mappings);
    List<Mapping> leftIdentities = new LinkedList<>();
    List<Mapping> rightIdentities = new LinkedList<>();
    for (Mapping mapping:mappings) {
      if (mappings.stream().allMatch(
        other -> other.equals(functionsUtil.composeMappings(
          mapping, other
        ))
      )) {
        leftIdentities.add(mapping);
      }
      if (mappings.stream().allMatch(
        other -> other.equals(functionsUtil.composeMappings(
          other, mapping
        ))
      )) {
        rightIdentities.add(mapping);
      }
      if (!leftIdentities.isEmpty() && !rightIdentities.isEmpty()) {
        mappingUtil.mapIdentity(mapping, "I");
        break;
      }
    }
    if (mappingUtil.missingIdentity()) {
      leftIdentities.forEach(leftIdentity -> mappingUtil.map(leftIdentity, "L"));
      rightIdentities.forEach(leftIdentity -> mappingUtil.map(leftIdentity, "R"));
      mappingUtil.mapRemaining();
      List<Mapping> mapped = mappingUtil.getMapped();
      return BinaryOperatorSummary.builder()
               .elements(getElements(mapped))
               .operator(getBinOp(mapped))
               .build();
    }
    
    Set<MappingCycle> mappingCycles = new HashSet<>();
    for (Mapping mapping:mappings) {
      List<Mapping> cycleElements = new LinkedList<>();
      cycleElements.add(mapping);
      Mapping next = functionsUtil.composeMappings(mapping, mapping);
      while (!cycleElements.contains(next)) {
        cycleElements.add(next);
        next = functionsUtil.composeMappings(mapping, next);
      }
      MappingCycle candidateCycle = cycleUtils.createMappingCycle(cycleElements);
      if (mappingCycles.stream().anyMatch(
        existingCycle -> existingCycle.isParentOf(candidateCycle)
      )) {
        continue;
      }
      mappingCycles.removeIf(candidateCycle::isParentOf);
      mappingCycles.add(candidateCycle);
    }
    
    mappingCycles.stream()
      .sorted(
        Comparator.comparing(
          AbstractCycle::toString
        )
      )
      .sorted(
        Comparator.comparing(
          AbstractCycle::getSize
        )
      )
      .forEach(mappingUtil::mapCycle);
    List<Mapping> mapped = mappingUtil.getMapped();
    
    return BinaryOperatorSummary
             .builder()
             .operator(getBinOp(mapped))
             .elements(getElements(mapped))
             .cycles(mappingCycles.stream()
                       .map(mappingCycle -> cycleUtils.convertToIntCycle(
                         mapped::indexOf,
                         mappingCycle
                       )).collect(Collectors.toSet()))
             .build();
  }
  
  public BinaryOperator createBinaryOperator(
    Element[] elements, BiFunction<Integer, Integer, Integer> operator
  ) {
    return createBinaryOperator(OperatorSymbol.DEFAULT, elements, operator);
  }
  
  public BinaryOperator createBinaryOperator(
    OperatorSymbol operatorSymbol, Element[] elements, BiFunction<Integer, Integer, Integer> operator
  ) {
    
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
