package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Element implements Comparable<Element> {
  public static Element I = Element.from("I");
  
  @EqualsAndHashCode.Include
  private final String ascii;
  private final String base;
  private final int pow;
  
  public Element(String ascii, String base, int pow) {
    this.ascii = ascii;
    this.base = base;
    this.pow = pow;
  }
  
  public static Element from(String base) {
    return from(base, 1);
  }
  
  public static Element from(String base, int power) {
    if (power == 1) {
      return new Element(base, base, 1);
    }
    if (power == 0) {
      throw new RuntimeException("Cannot construct elements with 0 power.");
    }
    return new Element(
      base + power,
      base, power
    );
  }
  
  public Element equivalenceClass() {
    return new Element(
      String.format("[%s]", ascii),
      base, pow
    );
  }
  
  public String getLatex() {
    if (pow == 1) {
      return base;
    }
    return String.format("%s^%d", base, pow);
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
