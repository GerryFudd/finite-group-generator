package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Data;

@Data
public class GroupAsLatex {
  private LatexCellSpec operatorAsLatex;
  private LatexCellSpec[] latexElements;
  private int[][] multiplicationTable;
}
