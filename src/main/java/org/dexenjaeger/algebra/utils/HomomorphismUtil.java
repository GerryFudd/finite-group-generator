package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.categories.morphisms.Automorphism;
import org.dexenjaeger.algebra.categories.morphisms.ConcreteAutomorphism;
import org.dexenjaeger.algebra.categories.morphisms.ConcreteHomomorphism;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.ConcreteGroup;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.OrderedPair;
import org.dexenjaeger.algebra.validators.AutomorphismValidator;
import org.dexenjaeger.algebra.validators.BinaryOperatorValidator;
import org.dexenjaeger.algebra.validators.GroupValidator;
import org.dexenjaeger.algebra.validators.HomomorphismValidator;
import org.dexenjaeger.algebra.validators.MonoidValidator;
import org.dexenjaeger.algebra.validators.SemigroupValidator;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomomorphismUtil {
  private static Validator<Homomorphism> homomorphismValidator;
  private static Validator<Automorphism> automorphismValidator;
  private static Validator<Group> groupValidator;
  
  private static void initValidators() {
    homomorphismValidator = new HomomorphismValidator();
    automorphismValidator = new AutomorphismValidator(homomorphismValidator);
    groupValidator = new GroupValidator(new MonoidValidator(new SemigroupValidator(new BinaryOperatorValidator())));
  }
  
  private static RuntimeException getInvalidCycleException(List<String> cycle) {
    return new RuntimeException(String.format(
      "Cycle is not valid: %s", String.join(", ", cycle)
    ));
  }
  private static OrderedPair<Group, Group> constructRangeAndKernel(Group domain, Function<String, String> act) {
    Map<String, String> rangeInversesMap = new HashMap<>();
    Map<String, String> domainLookupMap = new HashMap<>();
    Set<String> rangeElements = new HashSet<>();
    Map<Integer, Set<List<String>>> rangeCycles = new HashMap<>();
    Set<String> kernelElements = new HashSet<>();
    Map<Integer, Set<List<String>>> kernelCycles = new HashMap<>();
    
    String rangeIdentity = act.apply(domain.getIdentity());
    
    rangeInversesMap.put(rangeIdentity, rangeIdentity);
    
    rangeElements.add(rangeIdentity);
    kernelElements.add(domain.getIdentity());
    
    domainLookupMap.put(rangeIdentity, domain.getIdentity());
    
    rangeCycles.put(1, Set.of(List.of(rangeIdentity)));
    kernelCycles.put(1, Set.of(List.of(domain.getIdentity())));
    
    for (Integer n:domain.getCycleSizes()) {
      for (List<String> cycle:domain.getNCycles(n)) {
        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
        LinkedList<String> linkedRangeCycle = new LinkedList<>();
        LinkedList<String> linkedRangeInverses = new LinkedList<>();
        LinkedList<String> linkedKernelCycle = new LinkedList<>();
        LinkedList<String> linkedKernelInverses = new LinkedList<>();
        String cycleId = linkedCycle.removeLast();
        if (!cycleId.equals(domain.getIdentity())) {
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
          linkedKernelCycle.addLast(domain.getIdentity());
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
        .inversesMap(rangeInversesMap)
        .cyclesMap(rangeCycles)
        .identity(rangeIdentity)
        .operatorSymbol("x")
        .elements(rangeElements)
        .operator((a, b) -> act.apply(domain.prod(
          domainLookupMap.get(a),
          domainLookupMap.get(b)
        )))
        .build(),
      ConcreteGroup.builder()
        .inversesMap(kernelElements.stream().collect(Collectors.toMap(
          Function.identity(),
          domain::getInverse
        )))
        .cyclesMap(kernelCycles)
        .identity(domain.getIdentity())
        .operatorSymbol(domain.getOperatorSymbol())
        .elements(kernelElements)
        .operator(domain::prod)
        .build()
    );
  }
  
  private static Homomorphism doCreateHomomorphism(
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
  
  public static Homomorphism createHomomorphism(
    Group domain, Group range, Group kernel, Function<String, String> act
  ) throws ValidationException {
    initValidators();
    groupValidator.validate(domain);
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  public static Homomorphism createHomomorphism(
    Group domain,
    Function<String, String> act
  ) throws ValidationException {
    initValidators();
    groupValidator.validate(domain);
    OrderedPair<Group, Group> rangeAndKernel = HomomorphismUtil.constructRangeAndKernel(
      domain, act
    );
    
    groupValidator.validate(rangeAndKernel.getLeft());
    groupValidator.validate(rangeAndKernel.getRight());
    return doCreateHomomorphism(
      domain, rangeAndKernel.getLeft(), rangeAndKernel.getRight(), act
    );
  }
  
  private static Set<List<String>> convertCycles(Set<List<String>> cycles, Function<String, String> func) {
    return cycles.stream()
             .map(cycle -> cycle.stream()
                             .map(func)
                             .collect(Collectors.toList()))
             .collect(Collectors.toSet());
  }
  
  public static Automorphism createAutomorphism(
    Group domain, Function<String, String> func
  ) throws ValidationException {
    Map<String, String> inverseFuncMap = domain.getElements().stream()
                                           .collect(Collectors.toMap(
                                             func,
                                             Function.identity()
                                           ));
    
    Map<String, String> rangeInverseMap = inverseFuncMap.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey,
      entry -> func.apply(domain.getInverse(entry.getValue()))
    ));
    
    return createAutomorphism(
      domain,
      ConcreteGroup.builder()
        .operatorSymbol("x")
        .identity(func.apply(domain.getIdentity()))
        .elements(inverseFuncMap.keySet())
        .inversesMap(rangeInverseMap)
        .cyclesMap(domain
                     .getCycleSizes()
                     .stream()
                     .map(n -> new OrderedPair<>(
                       n, convertCycles(domain.getNCycles(n), func)
                     ))
                     .collect(Collectors.toMap(
                       OrderedPair::getLeft,
                       OrderedPair::getRight
                     )))
        .operator((a, b) -> func.apply(domain.prod(
          inverseFuncMap.get(a),
          inverseFuncMap.get(b)
        )))
        .build(),
      func,
      inverseFuncMap::get
    );
  }
  
  public static Automorphism createAutomorphism(
    Group domain, Group range, Function<String, String> func, Function<String, String> inverse
  ) throws ValidationException {
    Automorphism automorphism = ConcreteAutomorphism.builder()
                                  .domain(domain)
                                  .range(range)
                                  .act(func)
                                  .inverseAct(inverse)
                                  .build();
    initValidators();
    automorphismValidator.validate(automorphism);
    return automorphism;
  }
}
