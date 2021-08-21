package org.dexenjaeger.algebra.utils.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileUtil {
  private static ObjectMapper mapper() {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }
  public static <T> Optional<List<T>> readAsListOfType(
    InputStream in, Class<T> type
  ) {
    try {
      T[] result = mapper().readerForArrayOf(type).readValue(in);
      return Optional.of(Arrays.asList(result));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }
  
  public static <T> boolean writeToJsonFile(
    Path fileName, T content
  ) {
    try {
      File parent = fileName.getParent().toFile();
      if (!parent.exists() && !parent.mkdirs()) {
        new RuntimeException("Failed to make parent directory.").printStackTrace();
        return false;
      }
      File target = fileName.toFile();
      if (target.exists()) {
        FileUtils.delete(target);
      }
      mapper().writeValue(target, content);
      return target.exists();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
