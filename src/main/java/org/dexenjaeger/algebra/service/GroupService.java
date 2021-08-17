package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.SortedGroupResult;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.spec.GroupSpec;
import org.dexenjaeger.algebra.utils.BinaryOperatorUtil;
import org.dexenjaeger.algebra.utils.CycleUtils;
import org.dexenjaeger.algebra.utils.Remapper;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupService {
  private final CycleUtils cycleUtils;
  private final Validator<Group> groupValidator;
  private final BinaryOperatorUtil binaryOperatorUtil;
  
  @Inject
  public GroupService(
    CycleUtils cycleUtils,
    Validator<Group> groupValidator,
    BinaryOperatorUtil binaryOperatorUtil
  ) {
    this.cycleUtils = cycleUtils;
    this.groupValidator = groupValidator;
    this.binaryOperatorUtil = binaryOperatorUtil;
  }
  
  public Group createCyclicGroup(String... elements) {
    return createCyclicGroup(elements, "*");
  }
  
  public Group createCyclicGroup(String[] elements, String operatorSymbol) {
    int n = elements.length;
    LinkedList<Integer> cycle = new LinkedList<>();
    Map<Integer, Integer> inverses = new HashMap<>();
    for (int i = 1; i < n; i++) {
      inverses.put(i, n - i);
      cycle.addLast(i);
    }
    inverses.put(0, 0);
    cycle.addLast(0);
    return Group.builder()
             .inversesMap(inverses)
             .maximalCycles(Set.of(cycleUtils.createIntCycle(cycle)))
             .identity(0)
             .operatorSymbol(operatorSymbol)
             .elements(elements)
             .size(n)
             .lookup(binaryOperatorUtil.createLookup(elements))
             .multiplicationTable(
               binaryOperatorUtil.getMultiplicationTable(
                 n, (a, b) -> (a + b) % n
               )
             )
             .build();
  }
  
  private <T> void setIfNull(Supplier<T> getter, Consumer<T> setter, Supplier<T> source) {
    if (getter.get() == null) {
      setter.accept(source.get());
    }
  }
  
  private void resolveMaximalCycles(GroupSpec spec) throws ValidationException {
    try {
      setIfNull(
        spec::getMaximalCycles,
        spec::setMaximalCycles,
        () -> binaryOperatorUtil.getMaximalCycles(
          spec.getElements().length,
          spec.getOperator()
        )
      );
    } catch (RuntimeException e) {
      throw new ValidationException("Couldn't generate cycles from binary operator.", e);
    }
  }
  
  private void completeGroupSpec(GroupSpec spec) throws ValidationException {
    setIfNull(
      spec::getLookup,
      spec::setLookup,
      () -> binaryOperatorUtil.createLookup(spec.getElements())
    );
    resolveMaximalCycles(spec);
    setIfNull(
      spec::getInversesMap,
      spec::setInversesMap,
      () -> binaryOperatorUtil.getInversesMap(
        spec.getElements().length,
        spec.getIdentity(),
        spec.getOperator()
      )
    );
  }
  
  public Group createGroup(
    GroupSpec spec
  ) throws ValidationException {
    completeGroupSpec(spec);
    Group result = Group.builder()
                     .inversesMap(spec.getInversesMap())
                     .maximalCycles(spec.getMaximalCycles())
                     .identity(spec.getIdentity())
                     .size(spec.getElements().length)
                     .elements(spec.getElements())
                     .operatorSymbol(spec.getOperatorSymbol())
                     .lookup(spec.getLookup())
                     .multiplicationTable(binaryOperatorUtil.getMultiplicationTable(
                       spec.getElements().length,
                       spec.getOperator()
                     ))
                     .build();
    
    groupValidator.validate(result);
    return result;
  }
  
  public SortedGroupResult createSortedGroup(
    GroupSpec spec
  ) throws ValidationException {
    resolveMaximalCycles(spec);
    Remapper remapper = Remapper.init(spec.getElements().length);
    Map<Integer, Set<Integer>> nCycleGenerators = new HashMap<>();
    for (IntCycle cycle: spec.getMaximalCycles().stream()
                           .flatMap(cycle -> Stream.concat(
                             Stream.of(cycle),
                             cycle.getSubCycles().stream()
                           )).collect(Collectors.toSet())) {
      nCycleGenerators.computeIfPresent(cycle.getSize(), (n, generators) -> {
        generators.addAll(cycle.getGenerators());
        return generators;
      });
      nCycleGenerators.computeIfAbsent(cycle.getSize(), n -> new HashSet<>(cycle.getGenerators()));
    }
    
    nCycleGenerators.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .map(Map.Entry::getValue)
      .forEach(nGenerators -> nGenerators.stream()
                                .sorted(Comparator.comparing(i -> spec.getElements()[i]))
                                .forEach(generator -> remapper.map(spec.getElements()[generator], generator)));
    
    
    return new SortedGroupResult(
      createGroup(
        new GroupSpec()
          .setOperatorSymbol(spec.getOperatorSymbol())
          .setIdentity(0) // The identity will be the only 1-cycle in a valid group
          .setElements(remapper.getElements())
          .setOperator(remapper.remapBiFunc(spec.getOperator()))
      ), remapper
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    String[] elements,
    int[][] multiplicationTable
  ) throws ValidationException {
    return constructGroupFromElementsAndMultiplicationTable(
      "*", elements, multiplicationTable
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    String operatorSymbol,
    String[] elements,
    int[][] multiplicationTable
  ) throws ValidationException {
    if (elements.length != multiplicationTable.length) {
      throw new RuntimeException("No.");
    }
    Group result = createSortedGroup(
      new GroupSpec()
        .setOperatorSymbol(operatorSymbol)
        .setElements(elements)
        .setOperator((i, j) -> multiplicationTable[i][j])
    ).getGroup();
    
    groupValidator.validate(result);
    return result;
  }
}
