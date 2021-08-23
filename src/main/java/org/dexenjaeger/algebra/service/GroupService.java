package org.dexenjaeger.algebra.service;

import com.google.common.collect.Sets;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.SortedGroupResult;
import org.dexenjaeger.algebra.model.binaryoperator.Element;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.model.spec.CyclicGroupSpec;
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
  
  public Group createCyclicGroup(String base, int n) {
    return createCyclicGroup(new CyclicGroupSpec()
    .setBase(base)
    .setN(n));
  }
  
  public Group createCyclicGroup(CyclicGroupSpec spec) {
    LinkedList<Integer> cycle = new LinkedList<>();
    Map<Integer, Integer> inverses = new HashMap<>();
    Element[] elements = new Element[spec.getN()];
    for (int i = 1; i < spec.getN(); i++) {
      inverses.put(i, spec.getN() - i);
      cycle.addLast(i);
      elements[i] = Element.from(spec.getBase(), i);
    }
    elements[0] = spec.getIdentityElement();
    inverses.put(0, 0);
    cycle.addLast(0);
    return Group.builder()
             .inversesMap(inverses)
             .maximalCycles(Set.of(cycleUtils.createIntCycle(cycle)))
             .identity(0)
             .operatorSymbol(spec.getOperatorSymbol())
             .elements(elements)
             .size(spec.getN())
             .lookup(binaryOperatorUtil.createLookup(elements))
             .multiplicationTable(
               binaryOperatorUtil.getMultiplicationTable(
                 spec.getN(), (a, b) -> (a + b) % spec.getN()
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
    Element[] elements,
    int[][] multiplicationTable
  ) {
    return constructGroupFromElementsAndMultiplicationTable(
      OperatorSymbol.DEFAULT, elements, multiplicationTable
    );
  }
  
  public Group constructGroupFromElementsAndMultiplicationTable(
    OperatorSymbol operatorSymbol,
    Element[] elements,
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
  
  private void validateSubGroup(Group group, List<Element> subset) {
    if (subset.isEmpty()) {
      throw new ValidationException("Subgroups may not be empty.");
    }
    ValidationException e = new ValidationException(String.format(
      "The set %s is not a subgroup.",
      subset
    ));
    for (Element x:subset) {
      if (!subset.contains(group.getInverse(x))) {
        throw e;
      }
      for (Element y:subset) {
        if (!subset.contains(group.prod(x, y))) {
          throw e;
        }
      }
    }
  }
  
  private Map<Element, Set<Element>> getValidatedCosets(Group group, List<Element> subgroup) {
    Map<Element, Set<Element>> leftCosets = new HashMap<>();
    for (Element a:group.getElementsDisplay()) {
      if (leftCosets.containsKey(a)) {
        continue;
      }
      boolean inCoset = false;
      for (Element b:subgroup) {
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
    for (Map.Entry<Element, Set<Element>> leftCoset: leftCosets.entrySet()) {
      for (Element el:leftCoset.getValue()) {
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
  
  public Group createQuotientGroup(Group group, List<Element> normalSubgroup) {
    validateSubGroup(group, normalSubgroup);
    Map<Element, Set<Element>> cosets = getValidatedCosets(group, normalSubgroup);
    List<Element> elements = new ArrayList<>(cosets.size());
    Map<Element, Integer> lookup = new HashMap<>();
    Element id = group.display(group.getIdentity());
    for (Map.Entry<Element, Set<Element>> entry:cosets.entrySet()) {
      Element representative = null;
      for (Element member:entry.getValue().stream().sorted().toArray(Element[]::new)) {
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
      .setElements(elements.stream()
                     .map(Element::equivalenceClass)
                     .toArray(Element[]::new))
      .setOperator((i, j) -> lookup.get(group.prod(elements.get(i), elements.get(j))))
    ).getGroup();
  }
}
