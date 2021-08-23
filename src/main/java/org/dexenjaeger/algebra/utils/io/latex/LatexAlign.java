package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Getter;

public enum LatexAlign {
  CENTER("c");
  
  @Getter
  private String symbol;
  
  LatexAlign(String symbol) {
    this.symbol = symbol;
  }
}
