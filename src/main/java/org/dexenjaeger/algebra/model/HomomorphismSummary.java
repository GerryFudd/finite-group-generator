package org.dexenjaeger.algebra.model;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HomomorphismSummary {
  private final Group domain;
  private final Map<String, Integer> rangeLookup = new HashMap<>();
  private final Map<String, Integer> kernelLookup = new HashMap<>();
  private final Map<Integer, Integer> actionBuilder = new HashMap<>();
  private final Map<Integer, Integer> inverseActionBuilder = new HashMap<>();
  
  public void addRangeValue(String a, int x) {
    if (rangeLookup.containsKey(a)) {
      return;
    }
    int i = rangeLookup.size();
    rangeLookup.put(a, i);
    actionBuilder.put(x, i);
    inverseActionBuilder.put(i, x);
  }
  
  public void addKernelValue(int x) {
    String display = domain.display(x);
    if (kernelLookup.containsKey(display)) {
      return;
    }
    kernelLookup.put(domain.display(x), kernelLookup.size());
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
  
  public int kernelProd(int i, int j) {
    String[] elements = getKernelElementsArray();
    return kernelLookup.get(
      domain.prod(elements[i], elements[j])
    );
  }
}
