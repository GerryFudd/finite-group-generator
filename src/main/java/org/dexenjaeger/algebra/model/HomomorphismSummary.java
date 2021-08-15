package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.cycle.IntCycle;
import org.dexenjaeger.algebra.utils.CycleUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HomomorphismSummary {
  private final CycleUtils cycleUtils = new CycleUtils();
  private final Group domain;
  private int rangeIdentity;
  private final Map<String, Integer> rangeLookup = new HashMap<>();
  private final Map<String, Integer> kernelLookup = new HashMap<>();
  private final Map<Integer, Integer> actionBuilder = new HashMap<>();
  private final Map<Integer, Integer> inverseActionBuilder = new HashMap<>();
  @Getter
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
    
    rangeCycles.add(cycleUtils.convertToIntCycle(rangeLookup::get, stringCycleElements));
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
    kernelCycles.add(cycleUtils.convertToIntCycle(kernelLookup::get, displayCycleElements));
    
    return this;
  }
  
  private String[] getElements(Map<String, Integer> lookup) {
    String[] result = new String[lookup.size()];
    for (Map.Entry<String, Integer> entry:lookup.entrySet()) {
      result[entry.getValue()] = entry.getKey();
    }
    return result;
  }
  
  public String[] getRangeElementsArray() {
    return getElements(rangeLookup);
  }
  
  public Set<IntCycle> getRangeMaximalCycles() {
    return rangeCycles.size() == 0 ?
             cycleUtils.createSingleIntCycle(rangeIdentity) :
             rangeCycles;
  }
  
  public int rangeProd(int i, int j) {
    return actionBuilder.get(domain.prod(
      inverseActionBuilder.get(i),
      inverseActionBuilder.get(j)
    ));
  }
  
  private String[] kernelElementsArray;
  
  public synchronized String[] getKernelElementsArray() {
    if (kernelElementsArray == null) {
      kernelElementsArray = getElements(kernelLookup);
    }
    return kernelElementsArray;
  }
  
  public Map<Integer, Integer> getKernelInversesMap() {
    return kernelElements.stream().collect(Collectors.toMap(
      kernelLookup::get,
      el -> kernelLookup.get(domain.display(
        domain.getInverse(domain.eval(el))
      ))
    ));
  }
  
  public Set<IntCycle> getKernelMaximalCycles() {
    return kernelCycles.size() == 0 ?
             cycleUtils.createSingleIntCycle(domain.getIdentity()) :
             kernelCycles;
  }
  
  public int kernelProd(int i, int j) {
    String[] elements = getKernelElementsArray();
    return kernelLookup.get(
      domain.prod(elements[i], elements[j])
    );
  }
}
