package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Getter;

public class LatexColumnSpec {
  @Getter
  private final LatexAlign columnAlignment;
  private final boolean vLineBefore;
  private final boolean vLineAfter;
  
  public LatexColumnSpec(LatexAlign columnAlignment) {
    this(columnAlignment, false, false);
  }
  
  public LatexColumnSpec(LatexAlign columnAlignment, boolean vLineBefore, boolean vLineAfter) {
    this.columnAlignment = columnAlignment;
    this.vLineBefore = vLineBefore;
    this.vLineAfter = vLineAfter;
  }
  
  public boolean hasVLineBefore() {
    return vLineBefore;
  }
  
  public boolean hasVLineAfter() {
    return vLineAfter;
  }
}
