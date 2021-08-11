package org.dexenjaeger.algebra.service;

import org.dexenjaeger.algebra.categories.morphisms.ConcreteHomomorphism;
import org.dexenjaeger.algebra.categories.morphisms.Homomorphism;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.HomomorphismSummary;
import org.dexenjaeger.algebra.model.cycle.StringCycle;
import org.dexenjaeger.algebra.validators.ValidationException;
import org.dexenjaeger.algebra.validators.Validator;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
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
    Group domain, Group range, Group kernel, Function<Integer, Integer> act
  ) throws ValidationException {
    groupValidator.validate(domain);
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(domain, range, kernel, act);
  }
  
  public Homomorphism createHomomorphism(
    Group domain,
    Function<Integer, String> act
  ) throws ValidationException {
    groupValidator.validate(domain);
    HomomorphismSummary summary = constructRangeAndKernel(
      domain, act
    );
    
    Group range = summary.getRange();
    Group kernel = summary.getKernel();
    
    groupValidator.validate(range);
    groupValidator.validate(kernel);
    return doCreateHomomorphism(
      domain, range, kernel, summary.getAct()
    );
  }
  
  private RuntimeException getInvalidCycleException(List<String> cycle) {
    return new RuntimeException(String.format(
      "Cycle is not valid: %s", String.join(", ", cycle)
    ));
  }
  
  private void addRangeElement(String a, int x, Map<String, Integer> lookup, Map<Integer, Integer> inverseImageLookup) {
    int i = lookup.size();
    lookup.put(a, i);
    inverseImageLookup.put(i, x);
  }
  
  private HomomorphismSummary constructRangeAndKernel(Group domain, Function<Integer, String> act) {
    HomomorphismSummary summary = new HomomorphismSummary(domain, act);
    
    String rangeIdentityDisplay = act.apply(domain.getIdentity());
    summary.setRangeIdentity(
      rangeIdentityDisplay, domain.getIdentity()
    );
    
    for (StringCycle cycle:domain.getMaximalCycles()) {
      LinkedList<String> rangeCycle = new LinkedList<>();
      List<String> kernelCycle = new LinkedList<>();
      LinkedList<String> domainCycle = new LinkedList<>(cycle.getElements());
      
      while (!domainCycle.isEmpty() && (
        rangeCycle.isEmpty() || !rangeCycle.getLast().equals(rangeIdentityDisplay)
      )) {
        String x = domainCycle.removeFirst();
        rangeCycle.addLast(act.apply(domain.eval(x)));
        if (rangeCycle.getLast().equals(rangeIdentityDisplay)) {
          kernelCycle.add(x);
        }
      }
      if (rangeCycle.size() > 1) {
        summary.addRangeMaximalCycle(
          rangeCycle, domain.eval(cycle.get(0))
        );
      }
      if (kernelCycle.size() > 0 &&
            !kernelCycle.get(0).equals(domain.getIdentityDisplay())) {
        String kernGen = kernelCycle.get(0);
        String kernNext = domain.prod(kernGen, kernGen);
        while (!kernNext.equals(kernGen)) {
          kernelCycle.add(kernNext);
          kernNext = domain.prod(kernGen, kernNext);
        }
        if (kernelCycle.size() > 1) {
          summary.addKernelCycle(kernelCycle);
        }
      }
    }
    
//    for (Integer n:domain.getCycleSizes()) {
//      for (List<String> cycle:domain.getNCycles(n)) {
//        LinkedList<String> linkedCycle = new LinkedList<>(cycle);
//        LinkedList<String> linkedRangeCycle = new LinkedList<>();
//        LinkedList<String> linkedRangeInverses = new LinkedList<>();
//        LinkedList<String> linkedKernelCycle = new LinkedList<>();
//        LinkedList<String> linkedKernelInverses = new LinkedList<>();
//        String cycleId = linkedCycle.removeLast();
//        if (!cycleId.equals(domain.getIdentityDisplay())) {
//          throw getInvalidCycleException(cycle);
//        }
//        while (!linkedCycle.isEmpty()) {
//          String x = linkedCycle.removeFirst();
//          String y = act.apply(x);
//          if (!rangeLookup.contains(y)) {
//            inverseImageLookup.put(y, x);
//            rangeLookup.add(y);
//            linkedRangeCycle.addLast(y);
//
//            if (!linkedCycle.isEmpty()) {
//              String inverseX = linkedCycle.getLast();
//              if (!inverseX.equals(domain.getInverse(x))) {
//                throw getInvalidCycleException(cycle);
//              }
//              String inverseY = act.apply(inverseX);
//              rangeInversesMap.put(y, inverseY);
//              rangeInversesMap.put(inverseY, y);
//              inverseImageLookup.put(inverseY, inverseX);
//              rangeLookup.add(inverseY);
//              linkedRangeInverses.addFirst(inverseY);
//            } else {
//              rangeInversesMap.put(y, y);
//            }
//          } else if (y.equals(rangeIdentity)) {
//            kernelElements.add(x);
//            linkedKernelCycle.addLast(x);
//            if (!linkedCycle.isEmpty()) {
//              String inverseX = linkedCycle.removeLast();
//              if (!inverseX.equals(domain.getInverse(x))) {
//                throw getInvalidCycleException(cycle);
//              }
//              if (!act.apply(inverseX).equals(rangeIdentity)) {
//                throw new RuntimeException(String.format(
//                  "Invalid homomorphism. Kernel is not a subgroup since %s is in the kernel but its inverse %s is not.", x, inverseX
//                ));
//              }
//              linkedKernelInverses.addFirst(inverseX);
//              kernelElements.add(inverseX);
//            }
//          } else if (!linkedCycle.isEmpty()){
//            linkedCycle.removeLast();
//          }
//        }
//        if (!linkedRangeCycle.isEmpty()) {
//          while (!linkedRangeInverses.isEmpty()) {
//            linkedRangeCycle.addLast(linkedRangeInverses.removeFirst());
//          }
//          linkedRangeCycle.addLast(rangeIdentity);
//          rangeCycles.compute(linkedRangeCycle.size(), (key, cycles) -> {
//            if (cycles == null) {
//              cycles = new HashSet<>();
//            }
//            cycles.add(linkedRangeCycle);
//            return cycles;
//          });
//        }
//        if (!linkedKernelCycle.isEmpty()) {
//          while (!linkedKernelInverses.isEmpty()) {
//            linkedKernelCycle.addLast(linkedKernelInverses.removeFirst());
//          }
//          linkedKernelCycle.addLast(domain.getIdentityDisplay());
//          kernelCycles.compute(linkedKernelCycle.size(), (key, cycles) -> {
//            if (cycles == null) {
//              cycles = new HashSet<>();
//            }
//            cycles.add(linkedKernelCycle);
//            return cycles;
//          });
//        }
//      }
//    }
    return summary;
  }
}
