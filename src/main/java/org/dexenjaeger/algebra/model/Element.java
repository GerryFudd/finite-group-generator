package org.dexenjaeger.algebra.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Element implements Comparable<Element> {
  public static Element I = Element.from("I");
  
  @EqualsAndHashCode.Include
  private final String ascii;
  private final String latex;
  
  public Element(String ascii, String latex) {
    this.ascii = ascii;
    this.latex = latex;
  }
  
  public static Element from(String base) {
    return from(base, 1);
  }
  
  public static Element from(String base, int power) {
    if (power == 1) {
      return new Element(base, base);
    }
    if (power == 0) {
      throw new RuntimeException("Cannot construct elements with 0 power.");
    }
    return new Element(
      base + power,
      String.format("%s^{%d}", base, power)
    );
  }
  
  public Element equivalenceClass() {
    return new Element(
      String.format("[%s]", ascii),
      String.format("\\left[%s\\right]", latex)
    );
  }
  
  @Override
  public String toString() {
    return ascii;
  }
  
  @Override
  public int compareTo(Element o) {
    return getAscii().compareTo(o.getAscii());
  }
}
