package org.dexenjaeger.algebra.model;

import lombok.RequiredArgsConstructor;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.model.binaryoperator.Element;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HomomorphismSummary {
  private final Group domain;
  private final Map<Element, Integer> rangeLookup = new HashMap<>();
  private final Map<Element, Integer> kernelLookup = new HashMap<>();
  private final Map<Integer, Integer> actionBuilder = new HashMap<>();
  private final Map<Integer, Integer> inverseActionBuilder = new HashMap<>();
  
  public void addRangeValue(Element a, int x) {
    if (rangeLookup.containsKey(a)) {
      return;
    }
    int i = rangeLookup.size();
    rangeLookup.put(a, i);
    actionBuilder.put(x, i);
    inverseActionBuilder.put(i, x);
  }
  
  public void addKernelValue(int x) {
    Element display = domain.display(x);
    if (kernelLookup.containsKey(display)) {
      return;
    }
    kernelLookup.put(domain.display(x), kernelLookup.size());
  }
  
  private Element[] getElements(Map<Element, Integer> lookup) {
    Element[] result = new Element[lookup.size()];
    for (Map.Entry<Element, Integer> entry:lookup.entrySet()) {
      result[entry.getValue()] = entry.getKey();
    }
    return result;
  }
  
  public Element[] getRangeElementsArray() {
    return getElements(rangeLookup);
  }
  
  public int rangeProd(int i, int j) {
    return actionBuilder.get(domain.prod(
      inverseActionBuilder.get(i),
      inverseActionBuilder.get(j)
    ));
  }
  
  private Element[] kernelElementsArray;
  
  public synchronized Element[] getKernelElementsArray() {
    if (kernelElementsArray == null) {
      kernelElementsArray = getElements(kernelLookup);
    }
    return kernelElementsArray;
  }
  
  public int kernelProd(int i, int j) {
    Element[] elements = getKernelElementsArray();
    return kernelLookup.get(
      domain.prod(elements[i], elements[j])
    );
  }
}
