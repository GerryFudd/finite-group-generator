package org.dexenjaeger.algebra.utils.io.csv;

import lombok.Data;

@Data
public class GroupAsCsv {
  private String[] asciiElements;
  private String asciiOperatorSymbol;
  private int[][] multiplicationTable;
}
