package org.dexenjaeger.algebra;

import com.google.inject.Guice;
import org.dexenjaeger.algebra.categories.objects.group.Group;
import org.dexenjaeger.algebra.generators.SymmetryGroupGenerator;
import org.dexenjaeger.algebra.model.cycle.Cycle;
import org.dexenjaeger.algebra.model.dto.CycleDto;
import org.dexenjaeger.algebra.model.dto.GroupDto;
import org.dexenjaeger.algebra.model.spec.SymmetryGroupExportSpec;
import org.dexenjaeger.algebra.utils.io.FileType;
import org.dexenjaeger.algebra.utils.io.FileUtil;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
  private static final String OUT_DIR = "results";
  private static final String IN_DIR = "groupSpecs";
  private static final String SYMMETRY_GROUPS_SPEC = "symmetryGroups.json";
  
  public static void main(String... args) {
    SymmetryGroupGenerator symmetryGroupGenerator = Guice.createInjector(
      new AlgebraModule()
    ).getInstance(SymmetryGroupGenerator.class);
    List<SymmetryGroupExportSpec> specs = FileUtil.readAsListOfType(
      Main.class.getResourceAsStream(
        String.format("/%s/%s", IN_DIR, SYMMETRY_GROUPS_SPEC)
      ),
      SymmetryGroupExportSpec.class
    ).orElseThrow(() -> new RuntimeException("Couldn't locate symmetry groups spec file."));
    for (SymmetryGroupExportSpec spec:specs) {
      Group group = symmetryGroupGenerator.createSymmetryGroup(spec.getElementsCount());
      if (spec.getFileType() == FileType.JSON) {
        if (!FileUtil.writeToJsonFile(
          Paths.get(
            OUT_DIR, FileType.JSON.getFullFileName(spec.getFileName())
          ),
          new GroupDto()
            .setOperatorSymbol(spec.getOperatorSymbol())
            .setMultiplicationTable(group.getMultiplicationTable())
            .setMaximalCycles(
              group.getMaximalCycles()
              .stream()
              .sorted(Comparator.comparing(Cycle::getSize))
              .map(cycle -> new CycleDto()
                .setSize(cycle.getSize())
                              .setGenerator(cycle.get(0))
                .setGeneratorSymbol(group.display(cycle.get(0))))
              .collect(Collectors.toList())
            )
        )) {
          new RuntimeException(String.format("Failed to write spec\n%s", spec)).printStackTrace();
        }
      }
    }
    
    
  }
}
