package org.dexenjaeger.algebra.utils.env;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

@Slf4j
public class EnvUtils {
  private static Properties properties;
  private static Optional<Properties> properties() {
    if (properties == null) {
      try {
        Properties result = new Properties();
        result.load(new FileReader("app.properties"));
        properties = result;
      } catch (IOException e) {
        log.debug("Failed to locate properties file.", e);
      }
    }
    return Optional.ofNullable(properties);
  }
  
  private static String convertToProp(String envName) {
    return Optional.ofNullable(envName)
             .map(s -> s.replace('_', '.').toLowerCase())
             .orElse(null);
  }
  
  private static String resolveProp(String envName) {
    return properties()
             .map(props -> props.getProperty(convertToProp(envName)))
             .orElse(null);
  }
  
  private static String resolve(String envName) {
    return Optional.ofNullable(System.getenv(envName))
             .orElseGet(() -> resolveProp(envName));
  }
  
  public static Optional<String> get(String envName) {
    return Optional.ofNullable(envName).map(EnvUtils::resolve);
  }
  
  public static String getOrElse(String envName) {
    return getOrElse(envName, () -> new RuntimeException(String.format(
      "Could not resolve environment variable %s or system property %s",
      envName, convertToProp(envName)
    )));
  }
  
  public static String getOrElse(
    String envName, Supplier<RuntimeException> s
  ) {
    return Optional.ofNullable(envName).map(EnvUtils::resolve)
             .orElseThrow(s);
  }
}
