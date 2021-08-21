package org.dexenjaeger.algebra.model.spec;

import lombok.Data;
import org.dexenjaeger.algebra.utils.io.FileType;

@Data
public class SymmetryGroupExportSpec {
  private Integer elementsCount;
  private String operatorSymbol;
  private String fileName;
  private FileType fileType;
}
