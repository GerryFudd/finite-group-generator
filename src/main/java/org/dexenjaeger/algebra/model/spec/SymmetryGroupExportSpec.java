package org.dexenjaeger.algebra.model.spec;

import lombok.Data;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.utils.io.FileType;

@Data
public class SymmetryGroupExportSpec {
  private Integer elementsCount;
  private OperatorSymbol operatorSymbol;
  private String fileName;
  private FileType fileType;
}
