package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.ConcreteHomomorphism;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomomorphismService {
  private final GroupService groupService;
  private final Validator<Group> groupValidator;
  private final Validator<Homomorphism> homomorphismValidator;
  
  @Inject
  public HomomorphismService(
    GroupService groupService,
    Validator<Group> groupValidator,
    Validator<Homomorphism> homomorphismValidator
  ) {
    this.groupService = groupService;
    this.groupValidator = groupValidator;
    this.homomorphismValidator = homomorphismValidator;
  }
  
  private Homomorphism doCreateHomomorphism(
    Group domain, Group range, Group kernel, Function<String, String> act
  ) throws ValidationException {
    Homomorphism result = ConcreteHomomorphism.builder()
                            .domain(domain)
                            .range(range)
                            .kernel(kernel)
                            .act(act)
                            .build();
    homomorphismValidator.validate(result);
    return result;
  }
  
  public Homomorphism createHomomorphism(
    Group domain, Group range, Group kernel, Function<String, String> act
  ) throws ValidationException {
    groupValidator.validate(domain);
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  public Homomorphism createHomomorphism(
    Group domain,
    Function<String, String> act
  ) throws ValidationException {
    groupValidator.validate(domain);
    OrderedPair<Group, Group> rangeAndKernel = constructRangeAndKernel(
      domain, act
    );
    
    groupValidator.validate(rangeAndKernel.getLeft());
    groupValidator.validate(rangeAndKernel.getRight());
    return doCreateHomomorphism(
      domain, rangeAndKernel.getLeft(), rangeAndKernel.getRight(), act
    );
  }
  
  private RuntimeException getInvalidCycleException(List<String> cycle) {
    return new RuntimeException(String.format(
      "Cycle is not valid: %s", String.join(", ", cycle)
    ));
  }
  
  private OrderedPair<Group, Group> constructRangeAndKernel(Group domain, Function<String, String> act) {
    Map<String, String> rangeInversesMap = new HashMap<>();
    Map<String, String> domainLookupMap = new HashMap<>();
    Set<String> rangeElements = new HashSet<>();
    Map<Integer, Set<List<String>>> rangeCycles = new HashMap<>();
    Set<String> kernelElements = new HashSet<>();
    Map<Integer, Set<List<String>>> kernelCycles = new HashMap<>();
    
    String rangeIdentity = act.apply(domain.getIdentityDisplay());
    
    rangeInversesMap.put(rangeIdentity, rangeIdentity);
    
    rangeElements.add(rangeIdentity);
    kernelElements.add(domain.getIdentityDisplay());
    
    domainLookupMap.put(rangeIdentity, domain.getIdentityDisplay());
    
    rangeCycles.put(1, Set.of(List.of(rangeIdentity)));
    kernelCycles.put(1, Set.of(List.of(domain.getIdentityDisplay())));
    
    for (Integer n:domain.getCycleSizes()) {
      for (List<String> cycle:domain.getNCycles(n)) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        LinkedList<String> linkedRangeCycle = new LinkedList<>();
        LinkedList<String> linkedRangeInverses = new LinkedList<>();
        LinkedList<String> linkedKernelCycle = new LinkedList<>();
        LinkedList<String> linkedKernelInverses = new LinkedList<>();
        String cycleId = linkedCycle.removeLast();
        if (!cycleId.equals(domain.getIdentityDisplay())) {
          throw getInvalidCycleException(cycle);
        }
        while (!linkedCycle.isEmpty()) {
          String x = linkedCycle.removeFirst();
          String y = act.apply(x);
          if (!rangeElements.contains(y)) {
            domainLookupMap.put(y, x);
            rangeElements.add(y);
            linkedRangeCycle.addLast(y);
            
            if (!linkedCycle.isEmpty()) {
              String inverseX = linkedCycle.getLast();
              if (!inverseX.equals(domain.getInverse(x))) {
                throw getInvalidCycleException(cycle);
              }
              String inverseY = act.apply(inverseX);
              rangeInversesMap.put(y, inverseY);
              rangeInversesMap.put(inverseY, y);
              domainLookupMap.put(inverseY, inverseX);
              rangeElements.add(inverseY);
              linkedRangeInverses.addFirst(inverseY);
            } else {
              rangeInversesMap.put(y, y);
            }
          } else if (y.equals(rangeIdentity)) {
            kernelElements.add(x);
            linkedKernelCycle.addLast(x);
            if (!linkedCycle.isEmpty()) {
              String inverseX = linkedCycle.removeLast();
              if (!inverseX.equals(domain.getInverse(x))) {
                throw getInvalidCycleException(cycle);
              }
              if (!act.apply(inverseX).equals(rangeIdentity)) {
                throw new RuntimeException(String.format(
                  "Invalid homomorphism. Kernel is not a subgroup since %s is in the kernel but its inverse %s is not.", x, inverseX
                ));
              }
              linkedKernelInverses.addFirst(inverseX);
              kernelElements.add(inverseX);
            }
          } else if (!linkedCycle.isEmpty()){
            linkedCycle.removeLast();
          }
        }
        if (!linkedRangeCycle.isEmpty()) {
          while (!linkedRangeInverses.isEmpty()) {
            linkedRangeCycle.addLast(linkedRangeInverses.removeFirst());
          }
          linkedRangeCycle.addLast(rangeIdentity);
          rangeCycles.compute(linkedRangeCycle.size(), (key, cycles) -> {
            if (cycles == null) {
              cycles = new HashSet<>();
            }
            cycles.add(linkedRangeCycle);
            return cycles;
          });
        }
        if (!linkedKernelCycle.isEmpty()) {
          while (!linkedKernelInverses.isEmpty()) {
            linkedKernelCycle.addLast(linkedKernelInverses.removeFirst());
          }
          linkedKernelCycle.addLast(domain.getIdentityDisplay());
          kernelCycles.compute(linkedKernelCycle.size(), (key, cycles) -> {
            if (cycles == null) {
              cycles = new HashSet<>();
            }
            cycles.add(linkedKernelCycle);
            return cycles;
          });
        }
      }
    }
    
    return new OrderedPair<>(
      ConcreteGroup.builder()
        .displayInversesMap(rangeInversesMap)
        .cyclesMap(rangeCycles)
        .identityDisplay(rangeIdentity)
        .operatorSymbol("x")
        .elementsDisplay(rangeElements)
        .displayOperator((a, b) -> act.apply(domain.prod(
          domainLookupMap.get(a),
          domainLookupMap.get(b)
        )))
        .build(),
      ConcreteGroup.builder()
        .displayInversesMap(kernelElements.stream().collect(Collectors.toMap(
          Function.identity(),
          domain::getInverse
        )))
        .cyclesMap(kernelCycles)
        .identityDisplay(domain.getIdentityDisplay())
        .operatorSymbol(domain.getOperatorSymbol())
        .elementsDisplay(kernelElements)
        .displayOperator(domain::prod)
        .build()
    );
  }
}
