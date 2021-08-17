package org.dexenjaeger.algebra.utils;

import org.dexenjaeger.algebra.model.Mapping;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

public class FunctionsUtil {
  public String[] createImage(int size, Function<Integer, String> imageFunc) {
    LinkedList<String> imageList = new LinkedList<>();
    
    while (imageList.size() < size) {
      imageList.addLast(imageFunc.apply(imageList.size()));
    }
    
    return imageList.toArray(new String[0]);
  }
  
  public Mapping createMapping(int size, Function<Integer, Integer> act) {
    int[] mapping = new int[size];
    
    for (int i = 0; i < size; i++) {
      mapping[i] = act.apply(i);
    }
    return new Mapping(mapping);
  }
  
  public int[] createInverseMapping(int size, Function<Integer, Integer> act) {
    int[] inverseMapping = new int[size];
    
    for (int i = 0; i < size; i++) {
      inverseMapping[act.apply(i)] = i;
    }
    
    return inverseMapping;
  }
  
  public Set<Integer> getFixedElements(int size, Function<Integer, Integer> act) {
    Set<Integer> result = new HashSet<>();
    for (int i = 0; i < size; i++) {
      if (i == act.apply(i)) {
        result.add(i);
      }
    }
    return result;
  }
  
  public int[] composeMappings(int[] a, int[] b) {
    int[] prod = new int[b.length];
    for (int i = 0; i < b.length; i++) {
      prod[i] = a[b[i]];
    }
    return prod;
  }
  
  public Mapping composeMappings(Mapping a, Mapping b) {
    return new Mapping(composeMappings(a.getArray(), b.getArray()));
  }
}
