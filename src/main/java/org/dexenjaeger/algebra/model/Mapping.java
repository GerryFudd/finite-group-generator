package org.dexenjaeger.algebra.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class Mapping {
  @Getter
  private final int[] array;
  @Getter
  @Setter
  private String display;
  
  public Mapping(int[] array) {
    this.array = array;
  }
  
  public int size() {
    return array.length;
  }
  
  public int get(int i) {
    return array[i];
  }
  
  @Override
  public int hashCode() {
    return Arrays.hashCode(array);
  }
  
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Mapping)) {
      return false;
    }
    
    return Arrays.equals(
      array, ((Mapping) other).getArray()
    );
  }
  
  public String arrayString() {
    StringBuilder sb = new StringBuilder();
    for (int x : array) {
      sb.append(x);
    }
    return sb.toString();
  }
  
  @Override
  public String toString() {
    if (display != null) {
      return display;
    }
    return arrayString();
  }
}
