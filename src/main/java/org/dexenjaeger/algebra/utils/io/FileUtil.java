package org.dexenjaeger.algebra.utils.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.converter.GroupConverter;
import org.dexenjaeger.algebra.utils.io.csv.GroupAsCsv;
import org.dexenjaeger.algebra.utils.io.latex.GroupAsLatex;
import org.dexenjaeger.algebra.utils.io.latex.LatexAlign;
import org.dexenjaeger.algebra.utils.io.latex.LatexCellSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexColumnSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexRowSpec;
import org.dexenjaeger.algebra.utils.io.latex.LatexTableWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FileUtil {
  public void writeGroupAsType(
    Group group, FileType type, String fileName, String outputDir
  ) throws IOException {
    Path output = Paths.get(
      outputDir, type.getFullFileName(fileName)
    );
    switch (type) {
      case JSON:
        writeToJsonFile(output, GroupConverter.toDto(group));
        break;
      case LATEX:
        writeToLaTeXFile(output, GroupConverter.toLatex(group));
        break;
      case CSV:
        writeCsvFile(output, GroupConverter.toCsv(group));
        break;
      default:
        throw new RuntimeException(String.format(
          "File type %s not implemented", type.name()
        ));
    }
  }
  
  private ObjectMapper mapper() {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }
  
  public <T> Optional<T> readAsType(
    InputStream in, Class<T> type
  ) {
    try {
      log.debug("Reading stream of type {}[]", type.getName());
      T result = mapper().readValue(in, type);
      log.debug("Result read as array.");
      return Optional.of(result);
    } catch (IOException e) {
      log.warn(e.getMessage());
      return Optional.empty();
    }
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
  
  private  <T> void writeToJsonFile(
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
  
  private void writeToLaTeXFile(Path output, GroupAsLatex groupAsLatex) throws IOException {
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
  
  private void writeCsvFile(Path output, GroupAsCsv group) throws IOException {
    String[] header = new String[group.getAsciiElements().length + 1];
    header[0] = group.getAsciiOperatorSymbol();
    System.arraycopy(
      group.getAsciiElements(),
      0, header,
      1,
      group.getAsciiElements().length
    );
    CSVFormat format = CSVFormat.DEFAULT.builder()
                                 .setHeader(header)
      .setAutoFlush(true)
      .build();
    try (CSVPrinter printer = format.print(output, StandardCharsets.UTF_8)) {
      for (int j = 0; j < group.getMultiplicationTable().length; j++) {
        LinkedList<String> cells = new LinkedList<>();
        cells.addLast(group.getAsciiElements()[j]);
        for (int k = 0; k < group.getMultiplicationTable().length; k++) {
          cells.add(group.getAsciiElements()[group.getMultiplicationTable()[j][k]]);
        }
        printer.printRecord(cells);
      }
    }
  }
}
