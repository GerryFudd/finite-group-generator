package org.dexenjaeger.algebra.utils;

public class MappingException extends RuntimeException {
  public MappingException(String message) {
    this(message, null);
  }
  public MappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
