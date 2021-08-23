package org.dexenjaeger.algebra;

import lombok.extern.slf4j.Slf4j;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.converter.GroupConverter;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.spec.SymmetryGroupExportSpec;
import org.dexenjaeger.algebra.utils.io.FileUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class Application {
  private final SymmetryGroupGenerator symmetryGroupGenerator;
  private final FileUtil fileUtil;
  
  @Inject
  public Application(
    SymmetryGroupGenerator symmetryGroupGenerator,
    FileUtil fileUtil
  ) {
    this.symmetryGroupGenerator = symmetryGroupGenerator;
    this.fileUtil = fileUtil;
  }
  
  public void writeFilesFromSpecs(
    List<SymmetryGroupExportSpec> specs, String outputDir
  ) throws IOException {
    for (SymmetryGroupExportSpec spec:specs) {
      Group group = symmetryGroupGenerator.createSymmetryGroup(
        spec.getElementsCount(), spec.getOperatorSymbol()
      );
      Path output = Paths.get(
        outputDir, spec.getFileType().getFullFileName(spec.getFileName())
      );
      log.info("Processing spec {}", spec);
      switch (spec.getFileType()) {
        case JSON:
          fileUtil.writeToJsonFile(output, GroupConverter.toDto(group));
          break;
        case LATEX:
          fileUtil.writeToLaTeXFile(output, GroupConverter.toLatex(group));
          break;
        default:
          throw new RuntimeException(String.format(
            "File type %s not implemented", spec.getFileType().name()
          ));
      }
    }
  }
}
