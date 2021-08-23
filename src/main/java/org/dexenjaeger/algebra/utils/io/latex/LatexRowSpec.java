package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class LatexRowSpec {
  @ToString.Include
  private final List<LatexCellSpec> cellSpecs;
  
  public LatexRowSpec(List<LatexCellSpec> cellSpecs) {
    this.cellSpecs = cellSpecs;
  }
}
