package org.dexenjaeger.algebra;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.dexenjaeger.algebra.model.spec.SymmetryGroupExportSpec;
import org.dexenjaeger.algebra.utils.env.EnvUtils;
import org.dexenjaeger.algebra.utils.io.FileUtil;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class Main {
  private static final String OUT_DIR = EnvUtils.getOrElse("OUT_DIR");
  private static final String IN_DIR = EnvUtils.getOrElse("IN_DIR");
  private static final String SYMMETRY_GROUPS_SPEC = EnvUtils.getOrElse("SYMMETRY_GROUPS_SPEC");
  
  public static void main(String... args) throws IOException {
    Injector injector = Guice.createInjector(
      new AlgebraModule()
    );
    FileUtil fileUtil = injector.getInstance(FileUtil.class);
    
    for (SymmetryGroupExportSpec spec:fileUtil.readAsListOfType(
      new FileInputStream(
        String.format("/%s/%s", IN_DIR, SYMMETRY_GROUPS_SPEC)
      ),
      SymmetryGroupExportSpec.class
    )
                                            .orElseThrow(() -> new RuntimeException("Couldn't locate symmetry groups spec file."))) {
      fileUtil.writeSymmetryGroupFromSpec(spec, OUT_DIR);
    }
  }
}
