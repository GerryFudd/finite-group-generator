package org.dexenjaeger.algebra.model.binaryoperator;

import lombok.Getter;

public enum OperatorSymbol {
  DEFAULT("*", "\\ast"),
  ALTERNATE("x", "\\star"),
  COMPOSITION("o", "\\circ"),
  ADDITION("+", "+"),
  MULTIPLICATION("*", "\\cdot");
  
  @Getter
  private String ascii;
  @Getter
  private String latex;
  
  OperatorSymbol(String ascii, String latex) {
    this.ascii = ascii;
    this.latex = latex;
  }
  
  @Override
  public String toString() {
    return ascii;
  }
}
