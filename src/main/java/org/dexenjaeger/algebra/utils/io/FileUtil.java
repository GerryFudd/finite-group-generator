package org.dexenjaeger.algebra.utils.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dexenjaeger.algebra.utils.io.latex.GroupAsLatex;
import org.dexenjaeger.algebra.utils.io.latex.LatexAlign;
import org.dexenjaeger.algebra.utils.io.latex.LatexCellSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexColumnSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexRowSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexTableWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FileUtil {
  private ObjectMapper mapper() {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }
  
  public <T> Optional<List<T>> readAsListOfType(
    InputStream in, Class<T> type
  ) {
    try {
      log.debug("Reading stream of type {}[]", type.getName());
      T[] result = mapper().readerForArrayOf(type).readValue(in);
      log.debug("Result read as array.");
      return Optional.of(Arrays.asList(result));
    } catch (IOException e) {
      log.warn(e.getMessage());
      return Optional.empty();
    }
  }
  
  public <T> void writeToJsonFile(
    Path fileName, T content
  ) throws IOException {
    File parent = fileName.getParent().toFile();
    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("Failed to make parent directory.");
    }
    File target = fileName.toFile();
    if (target.exists()) {
      FileUtils.delete(target);
    }
    mapper().writeValue(target, content);
    if (!target.exists()) {
      throw new IOException("Failed to write file.");
    }
  }
  
  public void writeToLaTeXFile(Path output, GroupAsLatex groupAsLatex) throws IOException {
    File parentDirectory = output.getParent().toFile();
    if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
      throw new IOException("Couldn't create parent directory.");
    }
    
    int size = groupAsLatex.getLatexElements().length;
  
    LinkedList<LatexColumnSpec> columnSpecs = new LinkedList<>();
    columnSpecs.addLast(
      new LatexColumnSpec(
        LatexAlign.CENTER,
        false, true
      )
    );
    while (columnSpecs.size() < size + 1) {
      columnSpecs.addLast(new LatexColumnSpec(LatexAlign.CENTER));
    }
    
    LinkedList<LatexRowSpec> rowSpecs = new LinkedList<>();
    
    // Add headerRow
    LinkedList<LatexCellSpec> headerRow = new LinkedList<>();
    headerRow.addLast(groupAsLatex.getOperatorAsLatex());
    headerRow.addAll(List.of(groupAsLatex.getLatexElements()));
    rowSpecs.addLast(new LatexRowSpec(headerRow));
  
    for (int i = 0; i < size; i++) {
      LinkedList<LatexCellSpec> cellSpecs = new LinkedList<>();
      cellSpecs.addLast(groupAsLatex.getLatexElements()[i]);
      cellSpecs.addAll(
        Arrays.stream(groupAsLatex.getMultiplicationTable()[i])
        .mapToObj(x -> groupAsLatex.getLatexElements()[x])
        .collect(Collectors.toList())
      );
      rowSpecs.add(new LatexRowSpec(cellSpecs));
    }
    
    try (LatexTableWriter writer = LatexTableWriter.builder()
                                     .setTarget(output.toFile())
                                     .setColumnSpecs(columnSpecs)
                                     .setRowSpecs(rowSpecs)
                                     .build()) {
      writer.writeTable();
    }
  }
}
