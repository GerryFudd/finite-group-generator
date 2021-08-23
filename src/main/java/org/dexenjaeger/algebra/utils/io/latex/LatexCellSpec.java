package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LatexCellSpec {
  private final String content;
  private final boolean formula;
  
  public LatexCellSpec(String content) {
    this(content, true);
  }
  
  public LatexCellSpec(String content, boolean formula) {
    this.content = content;
    this.formula = formula;
  }
}
