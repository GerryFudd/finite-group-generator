package org.dexenjaeger.algebra.utils.io.latex;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class LatexTableWriter implements AutoCloseable {
  private final Writer writer;
  private final List<LatexColumnSpec> columnSpecs;
  private final Iterable<LatexRowSpec> rowSpecs;
  
  LatexTableWriter(
    Writer writer, List<LatexColumnSpec> columnSpecs,
    Iterable<LatexRowSpec> rowSpecs
  ) {
    this.writer = writer;
    this.columnSpecs = columnSpecs;
    this.rowSpecs = rowSpecs;
  }
  
  public static LatexTableWriterBuilder builder() throws IOException {
    return new LatexTableWriterBuilder();
  }
  // \begin{tabular}{c|cccccc}
  // $\circ$	&$I$		&$(01)$	&$(12)$	&$(02)$	&$(012)$	&$(021)$\\
  // \hline
  // $I$		&$I$		&$(01)$	&$(12)$	&$(02)$	&$(012)$	&$(021)$\\
  // $(01)$	&$(01)$	&$I$		&$(012)$	&$(021)$	&$(12)$	&$(02)$\\
  // $(12)$	&$(12)$	&$(021)$	&$I$		&$(012)$	&$(02)$	&$(01)$\\
  // $(02)$	&$(02)$	&$(012)$	&$(021)$	&$I$		&$(01)$	&$(12)$\\
  // $(012)$	&$(012)$	&$(02)$	&$(10)$	&$(12)$	&$(021)$	&$I$\\
  // $(021)$	&$(021)$	&$(12)$	&$(02)$	&$(01)$	&$I$		&$(012)$
  // \end{tabular}
  private LatexTableWriter begin() throws IOException {
    writer.append("\\begin{tabular}{");
    for (LatexColumnSpec spec:columnSpecs) {
      if (spec.hasVLineBefore()) {
        writer.append("|");
      }
      writer.append(spec.getColumnAlignment().getSymbol());
      if (spec.hasVLineAfter()) {
        writer.append("|");
      }
    }
    writer.append("}\n");
    return this;
  }
  
  private String getCellContent(LatexCellSpec cellSpec) {
    if (cellSpec.isFormula()) {
      return new StringBuilder()
        .append("$")
        .append(cellSpec.getContent())
        .append("$")
        .toString();
    }
    return cellSpec.getContent();
  }
  
  private LatexTableWriter middle() throws IOException {
    boolean headerWritten = false;
    for (LatexRowSpec rowSpec:rowSpecs) {
      if (rowSpec.getCellSpecs().size() > columnSpecs.size()) {
        throw new RuntimeException(String.format(
          "Column spec and row size mismatch. Row %s is larger than table width %d.",
          rowSpec, columnSpecs.size()
        ));
      }
      writer.append(
        rowSpec.getCellSpecs().stream()
          .map(this::getCellContent)
          .collect(Collectors.joining("&"))
      );
      writer.append("\\\\\n");
      if (!headerWritten) {
        writer.append("\\hline\n");
        headerWritten = true;
      }
    }
    return this;
  }
  
  private LatexTableWriter end() throws IOException {
    writer.append("\\end{tabular}\n");
    return this;
  }
  
  public void writeTable() throws IOException {
    begin()
      .middle()
      .end();
  }
  
  @Override
  public void close() throws IOException {
    writer.flush();
  }
}
