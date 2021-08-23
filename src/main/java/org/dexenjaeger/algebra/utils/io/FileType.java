package org.dexenjaeger.algebra.utils.io;

public enum FileType {
  JSON("json"), LATEX("tex"), CSV("csv");
  
  private final String extension;
  
  FileType(String extension) {
    this.extension = extension;
  }
  
  public String getFullFileName(String fileName) {
    return String.format("%s.%s", fileName, extension);
  }
}
