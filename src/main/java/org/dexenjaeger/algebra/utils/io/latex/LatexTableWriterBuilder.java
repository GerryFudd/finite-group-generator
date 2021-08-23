package org.dexenjaeger.algebra.utils.io.latex;

import lombok.Setter;
import org.dexenjaeger.algebra.utils.Builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Setter
public class LatexTableWriterBuilder implements Builder<LatexTableWriter> {
  private File target;
  private List<LatexColumnSpec> columnSpecs;
  private Iterable<LatexRowSpec> rowSpecs;
  
  @Override
  public LatexTableWriter build() {
    try {
      return new LatexTableWriter(
        new BufferedWriter(new FileWriter(target)),
        columnSpecs, rowSpecs
      );
    } catch (IOException e) {
      throw new RuntimeException("Can't create LaTeX table writer.", e);
    }
  }
}
