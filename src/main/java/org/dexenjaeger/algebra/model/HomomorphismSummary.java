package org.dexenjaeger.algebra.model;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.IntCycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HomomorphismSummary {
  private final Group domain;
  private final Function<Integer, String> act;
  private int rangeIdentity;
  private final Map<String, Integer> rangeLookup = new HashMap<>();
  private final Map<String, Integer> kernelLookup = new HashMap<>();
  private final Map<Integer, Integer> actionBuilder = new HashMap<>();
  private final Map<Integer, Integer> inverseActionBuilder = new HashMap<>();
  private final Map<Integer, Integer> rangeInversesMap = new HashMap<>();
  private final Set<String> kernelElements = new HashSet<>();
  
  private final Set<IntCycle> rangeCycles = new HashSet<>();
  private final Set<IntCycle> kernelCycles = new HashSet<>();
  
  public HomomorphismSummary setRangeIdentity(String rangeIdentityDisplay, int domainIdentity) {
    rangeIdentity = rangeLookup.size();
    rangeLookup.put(rangeIdentityDisplay, rangeIdentity);
    rangeInversesMap.put(rangeIdentity, rangeIdentity);
    
    kernelLookup.put(domain.getIdentityDisplay(), kernelElements.size());
    kernelElements.add(domain.getIdentityDisplay());
    
    actionBuilder.put(domainIdentity, rangeIdentity);
    inverseActionBuilder.put(rangeIdentity, domainIdentity);
    return this;
  }
  
  private void addRangeAndInverse(String a, String inv, int x) {
    int i = rangeLookup.size();
    rangeLookup.put(a, i);
    rangeLookup.put(inv, i + 1);
    rangeInversesMap.put(i, i + 1);
    rangeInversesMap.put(i + 1, i);
    
    actionBuilder.put(x, i);
    actionBuilder.put(domain.getInverse(x), i + 1);
    
    inverseActionBuilder.put(i, x);
    inverseActionBuilder.put(i + 1, domain.getInverse(x));
  }
  
  private void addRangeSelfInverse(String a, int x) {
    int i = rangeLookup.size();
    rangeLookup.put(a, i);
    rangeInversesMap.put(i, i);
    
    actionBuilder.put(x, i);
    inverseActionBuilder.put(i, x);
  }
  
  public HomomorphismSummary addRangeMaximalCycle(List<String> stringCycleElements, int domainGenerator) {
    int x = domain.getIdentity();
    LinkedList<String> cycleElements = new LinkedList<>(stringCycleElements);
    cycleElements.removeLast();
    while (!cycleElements.isEmpty()) {
      x = domain.prod(domainGenerator, x);
      String y = cycleElements.removeFirst();
      if (cycleElements.isEmpty()) {
        addRangeSelfInverse(y, x);
      } else {
        addRangeAndInverse(y, cycleElements.removeLast(), x);
      }
    }
    
    rangeCycles.add(IntCycle.builder()
                      .elements(stringCycleElements.stream()
                                  .map(rangeLookup::get)
                                  .collect(Collectors.toList()))
                      .build());
    return this;
  }
  
  public HomomorphismSummary addKernelCycle(List<String> displayCycleElements) {
    
    LinkedList<String> cycleElements = new LinkedList<>(displayCycleElements);
    
    cycleElements.removeLast();
    while (!cycleElements.isEmpty()) {
      String next = cycleElements.removeFirst();
      kernelLookup.put(next, kernelElements.size());
      kernelElements.add(next);
    }
    kernelCycles.add(IntCycle.builder()
                       .elements(
                         displayCycleElements.stream()
                         .map(kernelLookup::get)
                         .collect(Collectors.toList())
                       )
                       .build());
    
    return this;
  }
  
  private Set<IntCycle> getTrivialCycle(int identity) {
    return Set.of(IntCycle.builder().elements(identity).build());
  }
  
  private String[] getElements(Map<String, Integer> lookup) {
    String[] result = new String[lookup.size()];
    for (Map.Entry<String, Integer> entry:lookup.entrySet()) {
      result[entry.getValue()] = entry.getKey();
    }
    return result;
  }
  
  public Group getRange() {
    String[] elements = getElements(rangeLookup);
    return Group.builder()
             .inversesMap(rangeInversesMap)
             .maximalCycles(rangeCycles.size() == 0 ?
                              getTrivialCycle(rangeIdentity) :
                              rangeCycles)
             .identity(rangeIdentity)
             .operatorSymbol("x")
             .elements(elements)
             .lookup(rangeLookup)
             .operator((a, b) -> actionBuilder.get(domain.prod(
               inverseActionBuilder.get(a),
               inverseActionBuilder.get(b)
             )))
             .build();
  }
  
  public Group getKernel() {
    String[] elements = getElements(kernelLookup);
    return Group.builder()
             .inversesMap(kernelElements.stream().collect(Collectors.toMap(
               kernelLookup::get,
               el -> kernelLookup.get(domain.display(
                 domain.getInverse(domain.eval(el))
               ))
             )))
             .maximalCycles(kernelCycles.size() == 0 ?
                              getTrivialCycle(domain.getIdentity()) :
                              kernelCycles)
             .identity(kernelLookup.get(domain.getIdentityDisplay()))
             .operatorSymbol(domain.getOperatorSymbol())
             .elements(elements)
             .operator((i, j) -> kernelLookup.get(
               domain.prod(elements[i], elements[j])
             ))
             .build();
  }
  
  public Function<Integer, Integer> getAct() {
    return i -> rangeLookup.get(act.apply(i));
  }
}
