package org.dexenjaeger.algebra.utils.io;

import com.google.common.base.Charsets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.FileUtils;
import org.dexenjaeger.algebra.AlgebraModule;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.binaryoperator.OperatorSymbol;
import org.dexenjaeger.algebra.model.dto.ElementDto;
import org.dexenjaeger.algebra.model.dto.GroupDto;
import org.dexenjaeger.algebra.model.spec.CyclicGroupSpec;
import org.dexenjaeger.algebra.model.spec.SymmetryGroupExportSpec;
import org.dexenjaeger.algebra.service.GroupService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileUtilTest {
  private final Injector injector = Guice.createInjector(new AlgebraModule());
  private final FileUtil fileUtil = injector.getInstance(FileUtil.class);
  private final GroupService groupService = injector.getInstance(GroupService.class);
  private final SymmetryGroupGenerator symmetryGroupGenerator = injector.getInstance(SymmetryGroupGenerator.class);
  
  private final String snapshotsDir = "src/test/resources/output-snapshots";
  
  private final String snapshotJsonFile = "testJsonGroup.json";
  
  private final String testOutputDir = "build/test-generated/";
  
  private final String testOutputFile = "generatedGroup";
  
  private void compareToSnapshot(
    Path snapshot, Path testOutput
  ) throws IOException {
    if (!snapshot.toFile().exists()) {
      FileUtils.copyFile(
        testOutput.toFile(),
        snapshot.toFile()
      );
    }
    assertEquals(
      FileUtils.readFileToString(
        snapshot.toFile(), Charsets.UTF_8
      ),
      FileUtils.readFileToString(
        testOutput.toFile(), Charsets.UTF_8
      )
    );
  }
  
  @Test
  void readsListOfSpecsFromFile() {
    assertEquals(
      List.of(
        new SymmetryGroupExportSpec()
          .setFileName("TEST-FILE-NAME")
          .setFileType(FileType.JSON)
          .setOperatorSymbol(OperatorSymbol.COMPOSITION)
          .setElementsCount(3)
      ),
      fileUtil.readAsListOfType(
        getClass().getResourceAsStream(
          "/group-specs/testSymmetryGroupSpecs.json"
        ),
        SymmetryGroupExportSpec.class
      ).orElseGet(() -> {
        fail("Should be able to read file.");
        return null;
      })
    );
  }
  
  @Test
  void readsSnapshotFromFile() {
    assertEquals(
      new GroupDto()
        .setOperatorSymbol(OperatorSymbol.ALTERNATE.getJson())
        .setElements(List.of(
          new ElementDto(false)
            .setBase("I")
            .setPow(1),
          new ElementDto(false)
            .setBase("a")
            .setPow(1),
          new ElementDto(false)
            .setBase("a")
            .setPow(2)
        ))
        .setMultiplicationTable(new int[][]{
          {0, 1, 2},
          {1, 2, 0},
          {2, 0, 1}
        }),
      fileUtil.readAsType(
        getClass().getResourceAsStream("/output-snapshots/" + snapshotJsonFile),
        GroupDto.class
      ).orElseGet(() -> {
        fail("Should be able to read file.");
        return null;
      })
    );
  }
  
  @Test
  void writesJsonFile() throws IOException {
    fileUtil.writeGroupAsType(
      groupService.createCyclicGroup(
        new CyclicGroupSpec()
          .setBase("a")
          .setN(3)
          .setOperatorSymbol(OperatorSymbol.ALTERNATE)
      ),
      FileType.JSON,
      testOutputFile,
      testOutputDir
    );
    
    compareToSnapshot(
      Paths.get(snapshotsDir, snapshotJsonFile),
      Paths.get(
        testOutputDir,
        FileType.JSON.getFullFileName(testOutputFile)
      )
    );
  }
  
  @Test
  void writesLatexFile() throws IOException {
    fileUtil.writeGroupAsType(
      groupService.createCyclicGroup(
        new CyclicGroupSpec()
          .setBase("a")
          .setN(3)
          .setOperatorSymbol(OperatorSymbol.ADDITION)
      ),
      FileType.LATEX,
      testOutputFile,
      testOutputDir
    );
    
    String snapshotLatexFile = "snapshotLatexGroup.tex";
    compareToSnapshot(
      Paths.get(
        snapshotsDir,
        snapshotLatexFile
      ),
      Paths.get(
        testOutputDir,
        FileType.LATEX.getFullFileName(testOutputFile)
      )
    );
  }
  
  @Test
  void writesLatexFile_fromSymmetryGroup() throws IOException {
    String testSymmetryOutputFile = "generatedSymmetryGroup";
    fileUtil.writeGroupAsType(
      symmetryGroupGenerator.createSymmetryGroup(3),
      FileType.LATEX,
      testSymmetryOutputFile,
      testOutputDir
    );
    
    String snapshotSymmetryGroupFile = "snapshotSymmetryGroupFile.tex";
    compareToSnapshot(
      Paths.get(
        snapshotsDir,
        snapshotSymmetryGroupFile
      ),
      Paths.get(
        testOutputDir,
        FileType.LATEX.getFullFileName(testSymmetryOutputFile)
      )
    );
  }
}