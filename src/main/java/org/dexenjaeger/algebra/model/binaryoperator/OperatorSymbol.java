package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.Getter;

public enum OperatorSymbol {
  DEFAULT("*", "\\ast", "&#42;"),
  ALTERNATE("x", "\\star", "&#8902;"),
  COMPOSITION("o", "\\circ", "&#8728;"),
  ADDITION("+", "+", "&#43;"),
  MULTIPLICATION("*", "\\cdot", "&#8729;");
  
  @Getter
  private String ascii;
  @Getter
  private String latex;
  @Getter
  private String json;
  
  OperatorSymbol(String ascii, String latex, String json) {
    this.ascii = ascii;
    this.latex = latex;
    this.json = json;
  }
  
  @Override
  public String toString() {
    return ascii;
  }
}
