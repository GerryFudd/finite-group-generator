package org.dexenjaeger.algebra.service;

import com.google.common.collect.Sets;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
  
  private void resolveMaximalCycles(GroupSpec spec) {
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
  
  private void completeGroupSpec(GroupSpec spec) {
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
  ) {
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
  ) {
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
  ) {
    return constructGroupFromElementsAndMultiplicationTable(
      "*", elements, multiplicationTable
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    String operatorSymbol,
    String[] elements,
    int[][] multiplicationTable
  ) {
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
  
  private void validateSubGroup(Group group, List<String> subset) {
    if (subset.isEmpty()) {
      throw new ValidationException("Subgroups may not be empty.");
    }
    ValidationException e = new ValidationException(String.format(
      "The set %s is not a subgroup.",
      subset
    ));
    for (String x:subset) {
      if (!subset.contains(group.getInverse(x))) {
        throw e;
      }
      for (String y:subset) {
        if (!subset.contains(group.prod(x, y))) {
          throw e;
        }
      }
    }
  }
  
  private Map<String, Set<String>> getValidatedCosets(Group group, List<String> subgroup) {
    Map<String, Set<String>> leftCosets = new HashMap<>();
    for (String a:group.getElementsDisplay()) {
      if (leftCosets.containsKey(a)) {
        continue;
      }
      boolean inCoset = false;
      for (String b:subgroup) {
        if (leftCosets.containsKey(group.prod(a, b))) {
          leftCosets.computeIfPresent(group.prod(a, b), (key, val) -> {
            val.add(a);
            return val;
          });
          inCoset = true;
          break;
        }
      }
      if (inCoset) {
        continue;
      }
      leftCosets.computeIfAbsent(a, key -> Sets.newHashSet(a));
    }
    for (Map.Entry<String, Set<String>> leftCoset: leftCosets.entrySet()) {
      for (String el:leftCoset.getValue()) {
        if (subgroup.stream().noneMatch(x -> leftCoset.getKey().equals(group.prod(x, el)))) {
          throw new ValidationException(String.format(
            "The value %s belongs to [%sH] but not [H%s] for subgroup H=%s.",
            el, leftCoset.getKey(), leftCoset.getKey(), subgroup
          ));
        }
      }
    }
    return leftCosets;
  }
  
  public Group createQuotientGroup(Group group, List<String> normalSubgroup) {
    validateSubGroup(group, normalSubgroup);
    Map<String, Set<String>> cosets = getValidatedCosets(group, normalSubgroup);
    List<String> elements = new ArrayList<>(cosets.size());
    Map<String, Integer> lookup = new HashMap<>();
    String id = group.display(group.getIdentity());
    for (Map.Entry<String, Set<String>> entry:cosets.entrySet()) {
      String representative = null;
      for (String member:entry.getValue().stream().sorted().toArray(String[]::new)) {
        if (representative == null) {
          representative = member;
        }
        if (id.equals(member)) {
          representative = member;
        }
        lookup.put(member, elements.size());
      }
      elements.add(representative);
    }
    
    return createSortedGroup(
      new GroupSpec()
      .setOperatorSymbol(group.getOperatorSymbol())
      .setElements(elements.stream().map(el -> String.format("[%s]", el)).toArray(String[]::new))
      .setOperator((i, j) -> lookup.get(group.prod(elements.get(i), elements.get(j))))
    ).getGroup();
  }
}
